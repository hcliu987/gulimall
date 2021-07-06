package com.hc.gulimall.seckill.service.impl;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.hc.common.to.mq.SeckillOrderTo;
import com.hc.common.utils.R;
import com.hc.common.vo.MemberResponseVo;
import com.hc.gulimall.seckill.feign.CouponFeignService;
import com.hc.gulimall.seckill.feign.ProductFeignService;
import com.hc.gulimall.seckill.interceptor.LoginUserInterceptor;
import com.hc.gulimall.seckill.service.SeckillService;
import com.hc.gulimall.seckill.to.SeckillSkuRedisTo;
import com.hc.gulimall.seckill.vo.SeckillSessionWithSkusVo;
import com.hc.gulimall.seckill.vo.SkuInfoVo;
import com.sun.deploy.security.BlockedException;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SeckillServiceImpl implements SeckillService {
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private CouponFeignService couponFeignService;
    @Autowired
    private ProductFeignService productFeignService;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    private final String SESSION_CACHE_PREFIX = "seckill:sessions:";
    private final String SECKILL_CHARE_PREFIX = "seckill:skus";
    private final String SKU_STOCK_SEMAPHORE = "seckill:stock:";    //+商品随机码

    @Override
    public void uploadSeckillSkuLatest3Days() {
        //扫描最近三天需要参加秒杀活动的商品
        R lates3DaySession = couponFeignService.getLates3DaySession();
        if (lates3DaySession.getCode() == 0) {
            //上架商品
            List<SeckillSessionWithSkusVo> data = lates3DaySession.getData("data", new TypeReference<List<SeckillSessionWithSkusVo>>() {
            });
            //缓存到redis中
            saveSessionInfos(data);
            //缓存活动的关联信息商品
            saveSessionSkuInfo(data);
        }
    }

    private void saveSessionSkuInfo(List<SeckillSessionWithSkusVo> data) {
        data.stream().forEach(session -> {
            //准备hash操作
            BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(SECKILL_CHARE_PREFIX);
            session.getRelationSkus().stream().forEach(seckillSkuVo -> {
                //生成随机码
                String token = UUID.randomUUID().toString().replace("-", "");
                String redisKey = seckillSkuVo.getPromotionSessionId().toString() + "-" + seckillSkuVo.getSkuId().toString();
                if (!operations.hasKey(redisKey)) {
                    //缓存商品信息
                    SeckillSkuRedisTo redisTo = new SeckillSkuRedisTo();
                    Long skuId = seckillSkuVo.getSkuId();
                    //先查询sku的基本信息
                    R info = productFeignService.getSkuinfo(skuId);
                    if (info.getCode() == 0) {
                        SkuInfoVo skuInfo = info.getData("skuInfo", new TypeReference<SkuInfoVo>() {
                        });
                        redisTo.setSkuInfo(skuInfo);
                    }
                    //sku的秒杀信息
                    BeanUtils.copyProperties(seckillSkuVo, redisTo);
                    //设置当前商品的秒杀时间
                    redisTo.setStartTime(session.getStartTime().getTime());
                    redisTo.setEndTime(session.getEndTime().getTime());
                    //设置商品的随机码
                    String seckillValue = JSON.toJSONString(redisTo);
                    operations.put(seckillSkuVo.getPromotionSessionId().toString() + "-" + seckillSkuVo.getSkuId().toString(), seckillValue);
                    //如果当前这个场次的商品库存信息已经上架就不需要上架
                    //5、使用库存作为分布式Redisson信号量（限流）
                    // 使用库存作为分布式信号量
                    RSemaphore semaphore = redissonClient.getSemaphore(SKU_STOCK_SEMAPHORE + token);
                    //商品可以秒杀的数量作为信号量
                    semaphore.trySetPermits(seckillSkuVo.getSeckillCount());
                }

            });
        });
    }

    private void saveSessionInfos(List<SeckillSessionWithSkusVo> data) {
        data.stream().forEach(session -> {
            //获取当前活动的开始和结束时间的时间戳
            long startTime = session.getStartTime().getTime();
            long endTime = session.getEndTime().getTime();
            //存入到redis中的key
            String key = SESSION_CACHE_PREFIX + startTime + "_" + endTime;
            //判断redis中是否有该信息,如果没有才进行添加
            Boolean hasKey = redisTemplate.hasKey(key);
            if (!hasKey) {
                List<String> skuIds = session.getRelationSkus().stream().map(item -> item.getPromotionSessionId() + "-" + item.getSkuId().toString()).collect(Collectors.toList());
                redisTemplate.opsForList().leftPushAll(key, skuIds);
            }
        });

    }

    @SentinelResource(value = "getCurrentSeckillSkusResource", blockHandler = "blockHandler")
    @Override
    public List<SeckillSkuRedisTo> getCurrentSeckillSkus() {
        try (Entry entry = SphU.entry("seckillSkus")) {
            //1.确定当前属于那个秒杀场次
            long currentTime = System.currentTimeMillis();
            //从redis中查到所有key以seckill:sessions开头的数据
            Set<String> keys = redisTemplate.keys(SESSION_CACHE_PREFIX);
            for (String key : keys) {
                String replace = key.replace(SESSION_CACHE_PREFIX, "");
                String[] s = replace.split("_");
                //获取存入redis的开始时间
                long startTime = Long.parseLong(s[0]);
                //获取存入redis的结束时间
                long endTime = Long.parseLong(s[1]);
                //判断是否是当前秒杀场次
                if (currentTime >= startTime && currentTime <= endTime) {
                    List<String> range = redisTemplate.opsForList().range(key, -100, 100);
                    BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(SECKILL_CHARE_PREFIX);
                    List<String> listValue = hashOps.multiGet(range);
                    if (listValue != null && listValue.size() >= 0) {
                        List<SeckillSkuRedisTo> collect = listValue.stream().map(item -> {
                            String items = item;
                            SeckillSkuRedisTo redisTo = JSON.parseObject(items, SeckillSkuRedisTo.class);
                            return redisTo;
                        }).collect(Collectors.toList());
                        return collect;
                    }
                    break;
                }

            }
        } catch (BlockedException | BlockException e) {
            log.error("资源被限流P{}", e.getMessage());
        }
        return null;
    }

    @Override
    public SeckillSkuRedisTo getSkuSeckilInfo(Long skuId) {
        //1、找到所有需要秒杀的商品的key信息---seckill:skus
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(SECKILL_CHARE_PREFIX);
        //拿到所有的key
        Set<String> keys = hashOps.keys();
        if (keys != null && keys.size() > 0) {
            //4-45正则表达式进行匹配
            String reg = "\\d-" + skuId;
            for (String key : keys) {
                if (Pattern.matches(reg, key)) {
                    //从redis中取出数据
                    String redisValue = hashOps.get(key);
                    SeckillSkuRedisTo redisTo = JSON.parseObject(redisValue, SeckillSkuRedisTo.class);
                    //随机码
                    Long currentTime = System.currentTimeMillis();
                    Long startTime = redisTo.getStartTime();
                    Long endTime = redisTo.getEndTime();
                    //如果当前时间大于等于秒杀活动开始时间并且要小于活动结束时间
                    if (currentTime >= startTime && currentTime <= endTime) {
                        return redisTo;
                    }
                    redisTo.setRandomCode(null);
                    return redisTo;
                }
            }
        }
        return null;
    }

    @Override
    public String kill(String killId, String key, Integer num) throws InterruptedException {
        long s1 = System.currentTimeMillis();
        //获取当前用户的信息
        MemberResponseVo user = LoginUserInterceptor.loginUser.get();
        //1.获取当前秒杀商品的详细信息从Redis中获取
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(SECKILL_CHARE_PREFIX);
        String skuInfoValue = hashOps.get(killId);
        if (StringUtils.isEmpty(killId)) {
            return null;
        }
        //(合法性效验)
        SeckillSkuRedisTo redisTo = JSON.parseObject(skuInfoValue, SeckillSkuRedisTo.class);
        Long startTime = redisTo.getStartTime();
        Long endTime = redisTo.getEndTime();
        long currentTime = System.currentTimeMillis();
        //判断当前这个秒杀请求是否在活动时间区间内(效验时间的合法性)
        if (currentTime >= startTime && currentTime <= endTime) {
            //2.效验随机码和商品id
            String randomCode = redisTo.getRandomCode();
            String skuId = redisTo.getPromotionSessionId() + "-" + redisTo.getSkuId();
            if (randomCode.equals(key) && killId.equals(skuId)) {
                //验证购物数量是否合理和库存量是否充足
                Integer seckillLimit = redisTo.getSeckillLimit();

                //获取信号量
                String seckillCount = redisTemplate.opsForValue().get(SKU_STOCK_SEMAPHORE + randomCode);
                Integer count = Integer.valueOf(seckillCount);
                if (count > 0 && num <= seckillLimit && count > num) {
                    //4、验证这个人是否已经买过了（幂等性处理）,如果秒杀成功，就去占位。userId-sessionId-skuId
                    //SETNX 原子性处理
                    String redisKey = user.getId() + "-" + skuId;
                    //设置自动过期
                    long ttl = endTime - currentTime;
                    Boolean aBoolean = redisTemplate.opsForValue().setIfAbsent(redisKey, num.toString(), ttl, TimeUnit.MINUTES);
                    if(aBoolean){
                        //占位成功说明从来没有买过,分布式锁(获取信号量-1)
                        RSemaphore semaphore = redissonClient.getSemaphore(SKU_STOCK_SEMAPHORE + randomCode);
                        //TODO 秒杀成功,快速下单
                        boolean semaphoreCount = semaphore.tryAcquire(num, 100, TimeUnit.MILLISECONDS);
                        if(semaphoreCount){
                            //创建订单号和订单信息发送给mq
                            //秒杀成功 快速下单 发送消息 到mq
                            String timeId = IdWorker.getTimeId();
                            SeckillOrderTo orderTo = new SeckillOrderTo();
                            orderTo.setOrderSn(timeId);
                            orderTo.setMemberId(user.getId());
                            orderTo.setNum(num);
                            orderTo.setPromotionSessionId(redisTo.getPromotionSessionId());
                            orderTo.setSkuId(redisTo.getSkuId());
                            orderTo.setSeckillPrice(redisTo.getSeckillPrice());
                            rabbitTemplate.convertAndSend("order-event-exchange","order.seckill.order",orderTo);
                            long s2 = System.currentTimeMillis();
                            log.info("耗时。。。。"+(s2-s1));
                            return timeId;
                        }
                    }
                }
            }
        }
        long s3=System.currentTimeMillis();
        log.info("耗时。。。"+(s3-s1));
        return null;
    }
}

package com.hc.gulimall.seckill.scheduled;

import com.hc.gulimall.seckill.service.SeckillService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;


@Slf4j
@Service
public class SeckillScheduled {
    @Autowired
    private SeckillService seckillService;
    @Autowired
    private RedissonClient redissonClient;

    //秒杀商品上架功能的锁
    private  final  String upload_lock="seckill:uoload:lock";

    @Scheduled(cron = " 0 0 1/1 * * ?'")
    public  void uploadSeckillSkuLatest3Days(){
        //重复上架无需处理
        log.info("上架秒杀的商品");
        //分布式锁
        RLock lock = redissonClient.getLock(upload_lock);
        try {
            //加锁
            lock.lock(10, TimeUnit.SECONDS);
            seckillService.uploadSeckillSkuLatest3Days();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }
}

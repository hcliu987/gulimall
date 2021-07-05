package com.hc.gulimall.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hc.common.utils.R;
import com.hc.gulimall.cart.exception.CartExceptionHandler;
import com.hc.gulimall.cart.feign.ProductFeignService;
import com.hc.gulimall.cart.interceptor.CartInterceptor;
import com.hc.gulimall.cart.service.CartService;
import com.hc.gulimall.cart.to.UserInfoTo;
import com.hc.gulimall.cart.vo.CartItemVo;
import com.hc.gulimall.cart.vo.CartVo;
import com.hc.gulimall.cart.vo.SkuInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import com.alibaba.fastjson.TypeReference;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import static com.hc.common.constant.CartConstant.CART_PREFIX;

@Slf4j
@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private ProductFeignService productFeignService;
    @Autowired
    private ThreadPoolExecutor executor;

    @Override
    public CartItemVo addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException {
        //得到要操作的购物车信息
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        //判断redis中是否包含该商品信息
        String productRedisValue = (String) cartOps.get(skuId.toString());
        //如果没有就添加数据
        if (StringUtils.isEmpty(productRedisValue)) {
            //2.添加新的商品到购物车redis
            CartItemVo cart = new CartItemVo();
            //开启第一个异步任务
            CompletableFuture<Void> getSkuInfoFuture = CompletableFuture.runAsync(() -> {
                //1.远程查询当前商品要添加的信息
                R info = productFeignService.getInfo(skuId);
                SkuInfoVo skuInfo = info.getData("skuInfo", new TypeReference<SkuInfoVo>() {
                });
                //数据操作
                cart.setSkuId(skuInfo.getSkuId());
                cart.setTitle(skuInfo.getSkuTitle());
                cart.setImage(skuInfo.getSkuDefaultImg());
                cart.setPrice(skuInfo.getPrice());
                cart.setCount(num);
            }, executor);
            //开启第二个异步任务
            CompletableFuture<Void> getSkuAttrValuesFuture = CompletableFuture.runAsync(() -> {
                List<String> skuSaleAttrValues = productFeignService.getSkuSaleAttrValues(skuId);
                cart.setSkuAttrValues(skuSaleAttrValues);
            }, executor);
            //等待所有异步任务都完成
            CompletableFuture.allOf(getSkuInfoFuture, getSkuAttrValuesFuture).get();
            String cartItemJson = JSON.toJSONString(cart);
            cartOps.put(skuId.toString(), cartItemJson);
            return cart;
        } else {
            //购物车由此信息
            CartItemVo cartItemVo = JSON.parseObject(productRedisValue, CartItemVo.class);
            cartItemVo.setCount(cartItemVo.getCount() + num);
            //修改redis的数据
            String cartItemJson = JSON.toJSONString(cartItemVo);
            cartOps.put(skuId.toString(), cartItemJson);
            return cartItemVo;
        }
    }

    /**
     * 获取到我们要操作的购物车
     *
     * @return
     */
    private BoundHashOperations<String, Object, Object> getCartOps() {
        //得到当前用户的信息
        UserInfoTo userInfoTo = CartInterceptor.toThreadLocal.get();
        String cartKey = "";
        if (userInfoTo.getUserId() != null) {
            cartKey = CART_PREFIX + userInfoTo.getUserId();
        } else {
            cartKey = CART_PREFIX + userInfoTo.getUserKey();
        }
        //锁定指定的key操作redis
        BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(cartKey);
        return operations;
    }

    @Override
    public CartItemVo getCartItem(Long skuId) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        String redisValue = (String) cartOps.get(skuId.toString());
        CartItemVo cartItemVo = JSON.parseObject(redisValue, CartItemVo.class);
        return cartItemVo;
    }

    @Override
    public CartVo getCart() throws ExecutionException, InterruptedException {
        CartVo cartVo = new CartVo();
        UserInfoTo userInfoTo = CartInterceptor.toThreadLocal.get();
        if (userInfoTo.getUserId() != null) {
            //登陆
            String cartKey = CART_PREFIX + userInfoTo.getUserId();
            //临时购物车的键
            String tempCartKey = CART_PREFIX + userInfoTo.getUserKey();
            //如果临时购物车的数据还未进行合并
            List<CartItemVo> tempCartItems = getCartItems(tempCartKey);
            if (tempCartItems != null) {
                //临时购物车有数据需要进行合并操作
                for (CartItemVo item : tempCartItems) {
                    addToCart(item.getSkuId(), item.getCount());
                }
                clearCartInfo(tempCartKey);
            }
            //3.获取登陆后的购物车数据
            List<CartItemVo> cartItems = getCartItems(cartKey);
            cartVo.setItems(cartItems);
        } else {
            //没登陆
            String cartKey = CART_PREFIX + userInfoTo.getUserKey();
            //获取临时购物车里的所有数据
            List<CartItemVo> cartItems = getCartItems(cartKey);
            cartVo.setItems(cartItems);
        }
        return cartVo;
    }

    private List<CartItemVo> getCartItems(String cartKey) {
        BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(cartKey);
        List<Object> values = operations.values();
        if (values != null && values.size() > 0) {
            List<CartItemVo> cartItemVoStream = values.stream().map((obj) -> {

                String str = (String) obj;
                CartItemVo cartItemVo = JSON.parseObject(str, CartItemVo.class);
                return cartItemVo;
            }).collect(Collectors.toList());
            return cartItemVoStream;
        }
        return null;
    }

    @Override
    public void clearCartInfo(String cartKey) {
        redisTemplate.delete(cartKey);

    }

    @Override
    public void checkItem(Long skuId, Integer check) {
        //查询购物车的信息
        CartItemVo cartItem = getCartItem(skuId);
        //修改商品状态
        cartItem.setCheck(check == 1 ? true : false);
        //将序列化存入redis中
        String redisValue = JSON.toJSONString(cartItem);
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        cartOps.put(skuId.toString(), redisValue);
    }

    @Override
    public void changeItemCount(Long skuId, Integer num) {
        //查询购物车里面的信息
        CartItemVo cartItem = getCartItem(skuId);
        cartItem.setCount(num);
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        //存入到redis中
        String redisValue = JSON.toJSONString(cartItem);
        cartOps.put(skuId.toString(), redisValue);

    }

    @Override
    public void deleteIdCartInfo(Integer skuId) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        cartOps.delete(skuId.toString());
    }

    @Override
    public List<CartItemVo> getUserCartItems() {
        List<CartItemVo> cartItemVos = new ArrayList<>();
        //获取当前用户的信息
        UserInfoTo userInfoTo = CartInterceptor.toThreadLocal.get();
        if (userInfoTo.getUserId() == null) {
            return null;
        } else {
            //获取购物车项
            String cartKey = CART_PREFIX + userInfoTo.getUserId();
            //获取所有的
            List<CartItemVo> cartItems = getCartItems(cartKey);
            if (cartItems==null){
                throw  new CartExceptionHandler();
            }
            //筛选出选中的
            cartItemVos=  cartItems.stream().filter(items -> items.getCheck()).map(
                    item ->{
                        //更新为最新的价格，需要查询数据库
                        BigDecimal price = productFeignService.getPrice(item.getSkuId());
                        item.setPrice(price);
                        return item;
                    }
            ).collect(Collectors.toList());
        }
        return cartItemVos;
    }
}

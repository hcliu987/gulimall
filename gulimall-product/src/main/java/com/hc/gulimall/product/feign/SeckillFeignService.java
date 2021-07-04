package com.hc.gulimall.product.feign;

import com.hc.common.utils.R;
import com.hc.gulimall.product.fallback.SeckillFeignServiceFallBack;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value ="gulimall-seckill",fallback = SeckillFeignServiceFallBack.class )
public interface SeckillFeignService {
    @GetMapping(value = "/sku/seckill/{skuId}")
    R getSkuSeckilInfo(@PathVariable("skuId") Long skuId);
}

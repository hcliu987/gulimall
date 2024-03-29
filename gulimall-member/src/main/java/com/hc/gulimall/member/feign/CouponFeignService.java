package com.hc.gulimall.member.feign;

import com.hc.gulimall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("gulimall-coupon")
public interface CouponFeignService {

    /**
     * 测试openFeign
     */
    @RequestMapping("/coupon/coupon/member/list")
    R membercoupons();
}

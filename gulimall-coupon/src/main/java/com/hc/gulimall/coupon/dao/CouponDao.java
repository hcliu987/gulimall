package com.hc.gulimall.coupon.dao;

import com.hc.gulimall.coupon.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author liuhaicheng
 * @email hc.liu987@gmail.com
 * @date 2023-02-24 17:48:29
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}

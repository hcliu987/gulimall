package com.hc.gulimall.coupon.dao;

import com.hc.gulimall.coupon.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author hcliu
 * @email hc.liu987@gmail.com
 * @date 2021-06-28 20:24:03
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}

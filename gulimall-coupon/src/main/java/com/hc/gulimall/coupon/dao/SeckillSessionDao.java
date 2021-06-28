package com.hc.gulimall.coupon.dao;

import com.hc.gulimall.coupon.entity.SeckillSessionEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 秒杀活动场次
 * 
 * @author hcliu
 * @email hc.liu987@gmail.com
 * @date 2021-06-28 20:24:03
 */
@Mapper
public interface SeckillSessionDao extends BaseMapper<SeckillSessionEntity> {
	
}

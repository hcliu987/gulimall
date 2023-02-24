package com.hc.gulimall.order.dao;

import com.hc.gulimall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author liuhaicheng
 * @email hc.liu987@gmail.com
 * @date 2023-02-24 18:45:19
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}

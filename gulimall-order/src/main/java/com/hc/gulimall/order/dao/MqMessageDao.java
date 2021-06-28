package com.hc.gulimall.order.dao;

import com.hc.gulimall.order.entity.MqMessageEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 
 * 
 * @author hcliu
 * @email hc.liu987@gmail.com
 * @date 2021-06-28 20:42:48
 */
@Mapper
public interface MqMessageDao extends BaseMapper<MqMessageEntity> {
	
}

package com.hc.gulimall.ware.dao;

import com.hc.gulimall.ware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品库存
 * 
 * @author liuhaicheng
 * @email hc.liu987@gmail.com
 * @date 2023-02-24 18:42:26
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {
	
}

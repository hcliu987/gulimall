package com.hc.gulimall.product.dao;

import com.hc.gulimall.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author hcliu
 * @email hc.liu987@gmail.com
 * @date 2021-06-28 20:09:31
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}

package com.hc.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hc.gulimall.common.utils.PageUtils;
import com.hc.gulimall.product.entity.ProductAttrValueEntity;

import java.util.Map;

/**
 * spu属性值
 *
 * @author liuhaicheng
 * @email hc.liu987@gmail.com
 * @date 2023-02-24 17:34:04
 */
public interface ProductAttrValueService extends IService<ProductAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);
}


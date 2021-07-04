package com.hc.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hc.common.utils.PageUtils;
import com.hc.gulimall.product.entity.SpuInfoDescEntity;

import java.util.Map;

/**
 * spu信息介绍
 *
 * @author hcliu
 * @email hc.liu987@gmail.com
 * @date 2021-06-28 20:09:31
 */
public interface SpuInfoDescService extends IService<SpuInfoDescEntity> {

    PageUtils queryPage(Map<String, Object> params);
    void saveSpuInfoDesc(SpuInfoDescEntity spuInfoDescEntity);
}


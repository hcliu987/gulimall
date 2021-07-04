package com.hc.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hc.common.utils.PageUtils;
import com.hc.gulimall.product.entity.SpuImagesEntity;

import java.util.List;
import java.util.Map;

/**
 * spu图片
 *
 * @author hcliu
 * @email hc.liu987@gmail.com
 * @date 2021-06-28 20:09:31
 */
public interface SpuImagesService extends IService<SpuImagesEntity> {

    PageUtils queryPage(Map<String, Object> params);
    void saveImages(Long id, List<String> images);
}


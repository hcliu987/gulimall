package com.hc.gulimall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hc.common.utils.PageUtils;
import com.hc.gulimall.order.entity.MqMessageEntity;

import java.util.Map;

/**
 * 
 *
 * @author hcliu
 * @email hc.liu987@gmail.com
 * @date 2021-06-28 20:42:48
 */
public interface MqMessageService extends IService<MqMessageEntity> {

    PageUtils queryPage(Map<String, Object> params);
}


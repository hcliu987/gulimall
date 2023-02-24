package com.hc.gulimall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hc.gulimall.common.utils.PageUtils;
import com.hc.gulimall.order.entity.OrderEntity;

import java.util.Map;

/**
 * 订单
 *
 * @author liuhaicheng
 * @email hc.liu987@gmail.com
 * @date 2023-02-24 18:45:19
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);
}


package com.hc.gulimall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hc.gulimall.common.utils.PageUtils;
import com.hc.gulimall.order.entity.OrderReturnApplyEntity;

import java.util.Map;

/**
 * 订单退货申请
 *
 * @author liuhaicheng
 * @email hc.liu987@gmail.com
 * @date 2023-02-24 18:45:19
 */
public interface OrderReturnApplyService extends IService<OrderReturnApplyEntity> {

    PageUtils queryPage(Map<String, Object> params);
}


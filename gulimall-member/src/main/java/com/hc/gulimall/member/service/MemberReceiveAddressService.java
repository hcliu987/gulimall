package com.hc.gulimall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hc.gulimall.common.utils.PageUtils;
import com.hc.gulimall.member.entity.MemberReceiveAddressEntity;

import java.util.Map;

/**
 * 会员收货地址
 *
 * @author liuhaicheng
 * @email hc.liu987@gmail.com
 * @date 2023-02-24 17:14:00
 */
public interface MemberReceiveAddressService extends IService<MemberReceiveAddressEntity> {

    PageUtils queryPage(Map<String, Object> params);
}


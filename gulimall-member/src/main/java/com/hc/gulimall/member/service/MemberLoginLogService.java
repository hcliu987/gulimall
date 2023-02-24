package com.hc.gulimall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hc.gulimall.common.utils.PageUtils;
import com.hc.gulimall.member.entity.MemberLoginLogEntity;

import java.util.Map;

/**
 * 会员登录记录
 *
 * @author liuhaicheng
 * @email hc.liu987@gmail.com
 * @date 2023-02-24 17:14:00
 */
public interface MemberLoginLogService extends IService<MemberLoginLogEntity> {

    PageUtils queryPage(Map<String, Object> params);
}


package com.hc.gulimall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hc.common.utils.PageUtils;
import com.hc.gulimall.member.entity.MemberStatisticsInfoEntity;

import java.util.Map;

/**
 * 会员统计信息
 *
 * @author hcliu
 * @email hc.liu987@gmail.com
 * @date 2021-06-28 21:29:04
 */
public interface MemberStatisticsInfoService extends IService<MemberStatisticsInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);
}


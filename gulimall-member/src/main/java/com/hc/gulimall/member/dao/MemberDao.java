package com.hc.gulimall.member.dao;

import com.hc.gulimall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author liuhaicheng
 * @email hc.liu987@gmail.com
 * @date 2023-02-24 17:14:00
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}

package com.hc.gulimall.member.dao;

import com.hc.gulimall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author hcliu
 * @email hc.liu987@gmail.com
 * @date 2021-06-28 21:29:04
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}

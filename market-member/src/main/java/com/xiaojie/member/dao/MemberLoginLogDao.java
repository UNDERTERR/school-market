package com.xiaojie.member.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaojie.member.entity.MemberLoginLogEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员登录记录

 */
@Mapper
public interface MemberLoginLogDao extends BaseMapper<MemberLoginLogEntity> {
	
}

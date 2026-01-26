package com.xiaojie.member.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaojie.member.entity.MemberLevelEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员等级

 */
@Mapper
public interface MemberLevelDao extends BaseMapper<MemberLevelEntity> {
	
}

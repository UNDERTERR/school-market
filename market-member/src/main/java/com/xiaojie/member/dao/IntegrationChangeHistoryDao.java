package com.xiaojie.member.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaojie.member.entity.IntegrationChangeHistoryEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 积分变化历史记录

 */
@Mapper
public interface IntegrationChangeHistoryDao extends BaseMapper<IntegrationChangeHistoryEntity> {
	
}

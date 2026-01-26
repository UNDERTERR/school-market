package com.xiaojie.order.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaojie.order.entity.RefundInfoEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 退款信息

 */
@Mapper
public interface RefundInfoDao extends BaseMapper<RefundInfoEntity> {
	
}

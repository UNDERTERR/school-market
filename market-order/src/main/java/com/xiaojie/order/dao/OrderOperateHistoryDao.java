package com.xiaojie.order.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaojie.order.entity.OrderOperateHistoryEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单操作历史记录

 */
@Mapper
public interface OrderOperateHistoryDao extends BaseMapper<OrderOperateHistoryEntity> {
	
}

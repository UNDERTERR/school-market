package com.xiaojie.order.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaojie.order.entity.OrderItemEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单项信息

 */
@Mapper
public interface OrderItemDao extends BaseMapper<OrderItemEntity> {
	
}

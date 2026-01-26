package com.xiaojie.order.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaojie.order.entity.OrderSettingEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单配置信息

 */
@Mapper
public interface OrderSettingDao extends BaseMapper<OrderSettingEntity> {
	
}

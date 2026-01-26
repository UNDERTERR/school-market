package com.xiaojie.order.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaojie.order.entity.PaymentInfoEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 支付信息表

 */
@Mapper
public interface PaymentInfoDao extends BaseMapper<PaymentInfoEntity> {
	
}

package com.xiaojie.order.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaojie.order.entity.OrderEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 订单

 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {


    @Update("UPDATE oms_order SET status=#{code}, pay_type=#{payType} WHERE order_sn=#{orderSn} ")
    void updateOrderStatus(@Param("orderSn") String orderSn, @Param("code") Integer code, @Param("payType") Integer payType);
}

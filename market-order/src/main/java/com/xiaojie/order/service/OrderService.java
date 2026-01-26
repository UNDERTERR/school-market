package com.xiaojie.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaojie.common.to.mq.SeckillOrderTo;
import com.xiaojie.common.utils.PageUtils;
import com.xiaojie.order.entity.OrderEntity;
import com.xiaojie.order.vo.*;

import java.util.Map;

/**
 * 订单

 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);

    OrderConfirmVo confirmOrder();

    SubmitOrderResponseVo submitOrder(OrderSubmitVo submitVo);

    OrderEntity getOrderByOrderSn(String orderSn);

    void closeOrder(OrderEntity orderEntity);

    PageUtils getMemberOrderPage(Map<String, Object> params);

    PayVo getOrderPay(String orderSn);

    void handlerPayResult(PayAsyncVo payAsyncVo);

    void createSeckillOrder(SeckillOrderTo orderTo);
}


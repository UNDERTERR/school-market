package com.xiaojie.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaojie.common.to.mq.SeckillOrderTo;
import com.xiaojie.common.utils.PageUtils;
import com.xiaojie.common.utils.Query;
import com.xiaojie.order.dao.OrderDao;
import com.xiaojie.order.entity.OrderEntity;
import com.xiaojie.order.feign.CartFeignService;
import com.xiaojie.order.feign.MemberFeignService;
import com.xiaojie.order.feign.ProductFeignService;
import com.xiaojie.order.feign.WareFeignService;
import com.xiaojie.order.service.OrderItemService;
import com.xiaojie.order.service.OrderService;
import com.xiaojie.order.service.PaymentInfoService;
import com.xiaojie.order.vo.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {


    @Autowired
    private CartFeignService cartFeignService;

    @Autowired
    private MemberFeignService memberFeignService;

    @Autowired
    private WareFeignService wareFeignService;

    @Autowired
    private ThreadPoolExecutor executor;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ProductFeignService productFeignService;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private PaymentInfoService paymentInfoService;



    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>());
        return new PageUtils(page);
    }

    @Override
    public OrderConfirmVo confirmOrder() {
        return null;
    }

    @Override
    public SubmitOrderResponseVo submitOrder(OrderSubmitVo submitVo) {
        return null;
    }

    @Override
    public OrderEntity getOrderByOrderSn(String orderSn) {
        OrderEntity order_sn = this.getOne(new QueryWrapper<OrderEntity>().eq("order_sn", orderSn));
        return order_sn;
    }

    @Override
    public void closeOrder(OrderEntity orderEntity) {

    }

    @Override
    public PageUtils getMemberOrderPage(Map<String, Object> params) {
        return null;
    }

    @Override
    public PayVo getOrderPay(String orderSn) {
        return null;
    }

    @Override
    public void handlerPayResult(PayAsyncVo payAsyncVo) {

    }

    @Override
    public void createSeckillOrder(SeckillOrderTo orderTo) {

    }
}

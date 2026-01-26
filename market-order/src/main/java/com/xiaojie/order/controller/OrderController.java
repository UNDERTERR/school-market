package com.xiaojie.order.controller;

import com.xiaojie.common.exception.NoStockException;
import com.xiaojie.common.utils.PageUtils;
import com.xiaojie.common.utils.R;
import com.xiaojie.order.configuration.AlipayTemplate;
import com.xiaojie.order.entity.OrderEntity;
import com.xiaojie.order.service.OrderService;
import com.xiaojie.order.vo.OrderConfirmVo;
import com.xiaojie.order.vo.OrderSubmitVo;
import com.xiaojie.order.vo.PayVo;
import com.xiaojie.order.vo.SubmitOrderResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;



/**
 * 订单

 */
@RestController
@RequestMapping("order/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    AlipayTemplate alipayTemplate;
    /**
     * 列表
     */
    @GetMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = orderService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @GetMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		OrderEntity order = orderService.getById(id);

        return R.ok().put("order", order);
    }

    @GetMapping("/infoByOrderSn/{OrderSn}")
    public R infoByOrderSn(@PathVariable("OrderSn") String OrderSn){
        OrderEntity order = orderService.getOrderByOrderSn(OrderSn);

        return R.ok().put("order", order);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    public R save(@RequestBody OrderEntity order){
		orderService.save(order);

        return R.ok();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    public R update(@RequestBody OrderEntity order){
		orderService.updateById(order);

        return R.ok();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		orderService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }


    /**
     * 订单确认页面数据
     */
    @RequestMapping("/confirm")
    public R confirmOrder() {
        OrderConfirmVo confirmVo = orderService.confirmOrder();
        return R.ok().put("confirmOrder", confirmVo);
    }
    /**
     * 提交订单
     */
    @RequestMapping("/submit")
    public R submitOrder(@RequestBody OrderSubmitVo submitVo) {
        try {
            SubmitOrderResponseVo responseVo = orderService.submitOrder(submitVo);
            Integer code = responseVo.getCode();
            if (code == 0) {
                return R.ok().put("order", responseVo.getOrder());
            } else {
                String msg = "下单失败;";
                switch (code) {
                    case 1:
                        msg += "防重令牌校验失败";
                        break;
                    case 2:
                        msg += "商品价格发生变化";
                        break;
                }
                return R.error(msg);
            }
        } catch (Exception e) {
            if (e instanceof NoStockException) {
                return R.error("下单失败，商品无库存");
            }
            return R.error("下单失败");
        }
    }
    /**
     * 获取当前用户的所有订单
     */
    @RequestMapping("/memberOrders")
    public R memberOrder(@RequestParam(value = "pageNum", required = false, defaultValue = "0") Integer pageNum) {
        Map<String, Object> params = new HashMap<>();
        params.put("page", pageNum.toString());
        PageUtils page = orderService.getMemberOrderPage(params);
        return R.ok().put("pageUtil", page);
    }
    /**
     * 获取支付信息
     */
    @GetMapping("/payInfo/{orderSn}")
    public R getPayInfo(@PathVariable("orderSn") String orderSn) {
        try {
            System.out.println("接收到订单信息orderSn："+orderSn);
            PayVo payVo = orderService.getOrderPay(orderSn);
            String pay = alipayTemplate.pay(payVo);
            return R.ok().put("pay", pay);
        } catch (Exception e) {
            return R.error("获取支付信息失败：" + e.getMessage());
        }
    }

}

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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "订单管理", description = "订单相关接口")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    AlipayTemplate alipayTemplate;
/**
     * 列表
     */
    @GetMapping("/list")
    @Operation(summary = "分页查询订单", description = "根据条件分页查询订单列表")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = orderService.queryPage(params);

        return R.ok().put("page", page);
    }


/**
     * 信息
     */
    @GetMapping("/info/{id}")
    @Operation(summary = "获取订单详情", description = "根据订单ID获取订单详细信息")
    public R info(@Parameter(description = "订单ID") @PathVariable("id") Long id){
		OrderEntity order = orderService.getById(id);

        return R.ok().put("order", order);
    }

@GetMapping("/infoByOrderSn/{OrderSn}")
    @Operation(summary = "根据订单号查询订单", description = "根据订单号获取订单详细信息")
    public R infoByOrderSn(@Parameter(description = "订单号") @PathVariable("OrderSn") String OrderSn){
        OrderEntity order = orderService.getOrderByOrderSn(OrderSn);

        return R.ok().put("order", order);
    }

/**
     * 保存
     */
    @PostMapping("/save")
    @Operation(summary = "保存订单", description = "新增订单信息")
    public R save(@Parameter(description = "订单信息") @RequestBody OrderEntity order){
		orderService.save(order);

        return R.ok();
    }

/**
     * 修改
     */
    @PutMapping("/update")
    @Operation(summary = "更新订单", description = "更新订单信息")
    public R update(@Parameter(description = "订单信息") @RequestBody OrderEntity order){
		orderService.updateById(order);

        return R.ok();
    }

/**
     * 删除
     */
    @DeleteMapping("/delete")
    @Operation(summary = "删除订单", description = "根据订单ID删除订单信息")
    public R delete(@Parameter(description = "订单ID数组") @RequestBody Long[] ids){
		orderService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }


/**
     * 订单确认页面数据
     */
    @RequestMapping("/confirm")
    @Operation(summary = "订单确认", description = "获取订单确认页面所需的数据")
    public R confirmOrder() {
        OrderConfirmVo confirmVo = orderService.confirmOrder();
        return R.ok().put("confirmOrder", confirmVo);
    }
/**
     * 提交订单
     */
    @RequestMapping("/submit")
    @Operation(summary = "提交订单", description = "用户提交订单信息")
    public R submitOrder(@Parameter(description = "订单提交信息") @RequestBody OrderSubmitVo submitVo) {
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
    @Operation(summary = "获取用户订单", description = "获取当前用户的所有订单列表")
    public R memberOrder(@Parameter(description = "页码") @RequestParam(value = "pageNum", required = false, defaultValue = "0") Integer pageNum) {
        Map<String, Object> params = new HashMap<>();
        params.put("page", pageNum.toString());
        PageUtils page = orderService.getMemberOrderPage(params);
        return R.ok().put("pageUtil", page);
    }
/**
     * 获取支付信息
     */
    @GetMapping("/payInfo/{orderSn}")
    @Operation(summary = "获取支付信息", description = "根据订单号获取支付宝支付信息")
    public R getPayInfo(@Parameter(description = "订单号") @PathVariable("orderSn") String orderSn) {
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

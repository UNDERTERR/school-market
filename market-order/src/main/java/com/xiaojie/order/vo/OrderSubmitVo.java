package com.xiaojie.order.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "订单提交请求参数")
public class OrderSubmitVo {

    /** 收获地址的id **/
    @Schema(description = "收货地址ID", example = "1")
    private Long addrId;

    /** 支付方式 **/
    @Schema(description = "支付方式：1-支付宝，2-微信", example = "1")
    private Integer payType;
    //无需提交要购买的商品，去购物车再获取一遍
    //优惠、发票

    /** 防重令牌 **/
    @Schema(description = "防重复提交令牌", example = "abc123def456")
    private String orderToken;

    /** 应付价格 **/
    @Schema(description = "订单应付价格", example = "99.99")
    private BigDecimal payPrice;

    /** 订单备注 **/
    @Schema(description = "订单备注信息", example = "请尽快发货")
    private String remarks;

    //用户相关的信息，直接去session中取出即可
}

package com.xiaojie.cart.controller;

import com.xiaojie.cart.service.CartService;
import com.xiaojie.cart.vo.CartItemVo;
import com.xiaojie.cart.vo.CartVo;
import com.xiaojie.common.utils.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@Tag(name = "购物车管理", description = "购物车相关接口")
public class CartController {

    @Autowired
    private CartService cartService;

    // ==================== 查询接口 ====================

/**
     * 获取购物车列表
     * GET /api/cart/list
     */
    @GetMapping("/list")
    @Operation(summary = "获取购物车列表", description = "获取当前用户的完整购物车信息")
    public R getCartList() {  // ← 返回统一响应对象 R
        CartVo cartVo = cartService.getCart();
        return R.ok().setData(cartVo);
    }

/**
     * 获取购物车中某个商品
     * GET /api/cart/item/{skuId}
     */
    @GetMapping("/item/{skuId}")
    @Operation(summary = "获取购物车商品", description = "获取购物车中指定SKU的商品信息")
    public R getCartItem(@Parameter(description = "商品SKU ID") @PathVariable("skuId") Long skuId) {
        CartItemVo cartItemVo = cartService.getCartItem(skuId);
        return R.ok().setData(cartItemVo);
    }

/**
     * 获取选中的商品（用于结算）
     * GET /api/cart/checkedItems
     */
    @GetMapping("/checkedItems")
    @Operation(summary = "获取选中商品", description = "获取购物车中选中的商品列表，用于订单结算")
    public R getCheckedItems() {
        List<CartItemVo> checkedItems = cartService.getCheckedItems();
        return R.ok().setData(checkedItems);
    }

    // ==================== 操作接口 ====================

/**
     * 添加商品到购物车
     * POST /api/cart/add
     */
    @PostMapping("/add")
    @Operation(summary = "添加商品到购物车", description = "将指定数量的商品添加到购物车")
    public R addCartItem(@Parameter(description = "商品SKU ID") @RequestParam("skuId") Long skuId, 
                        @Parameter(description = "商品数量") @RequestParam("num") Integer num) {
        cartService.addCartItem(skuId, num);
        return R.ok("添加成功");
    }

/**
     * 更新商品选中状态
     * PUT /api/cart/check/{skuId}
     */
    @PutMapping("/check/{skuId}")
    @Operation(summary = "更新商品选中状态", description = "更新购物车中商品的选中状态")
    public R checkCart(@Parameter(description = "商品SKU ID") @PathVariable("skuId") Long skuId,
                       @Parameter(description = "是否选中：1-选中，0-未选中") @RequestParam("isChecked") Integer isChecked) {
        cartService.checkCart(skuId, isChecked);
        return R.ok();
    }

/**
     * 修改商品数量
     * PUT /api/cart/count/{skuId}
     */
    @PutMapping("/count/{skuId}")
    @Operation(summary = "修改商品数量", description = "更新购物车中商品的数量")
    public R changeItemCount(@Parameter(description = "商品SKU ID") @PathVariable("skuId") Long skuId,
                             @Parameter(description = "商品数量") @RequestParam("num") Integer num) {
        cartService.changeItemCount(skuId, num);
        return R.ok();
    }

/**
     * 删除商品
     * DELETE /api/cart/item/{skuId}
     */
    @DeleteMapping("/item/{skuId}")
    @Operation(summary = "删除购物车商品", description = "从购物车中删除指定商品")
    public R deleteItem(@Parameter(description = "商品SKU ID") @PathVariable("skuId") Long skuId) {
        cartService.deleteItem(skuId);
        return R.ok();
    }

//    /**
//     * 清空购物车
//     * DELETE /api/cart/clear
//     */
//    @DeleteMapping("/clear")
//    public R clearCart() {
//        cartService.clearCart();
//        return R.ok();
//    }
//
//    /**
//     * 获取购物车商品总数
//     * GET /api/cart/count
//     */
//    @GetMapping("/count")
//    public R getCartCount() {
//        Integer count = cartService.getCartCount();
//        return R.ok().setData(count);
//    }
}
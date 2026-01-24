package com.xiaojie.cart.controller;

import com.xiaojie.cart.service.CartService;
import com.xiaojie.cart.vo.CartItemVo;
import com.xiaojie.cart.vo.CartVo;
import com.xiaojie.common.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    // ==================== 查询接口 ====================

    /**
     * 获取购物车列表
     * GET /api/cart/list
     */
    @GetMapping("/list")
    public R getCartList() {  // ← 返回统一响应对象 R
        CartVo cartVo = cartService.getCart();
        return R.ok().setData(cartVo);
    }

    /**
     * 获取购物车中某个商品
     * GET /api/cart/item/{skuId}
     */
    @GetMapping("/item/{skuId}")
    public R getCartItem(@PathVariable("skuId") Long skuId) {
        CartItemVo cartItemVo = cartService.getCartItem(skuId);
        return R.ok().setData(cartItemVo);
    }

    /**
     * 获取选中的商品（用于结算）
     * GET /api/cart/checkedItems
     */
    @GetMapping("/checkedItems")
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
    public R addCartItem(@RequestParam("skuId") Long skuId, @RequestParam("num") Integer num) {
        cartService.addCartItem(skuId, num);
        return R.ok("添加成功");
    }

    /**
     * 更新商品选中状态
     * PUT /api/cart/check/{skuId}
     */
    @PutMapping("/check/{skuId}")
    public R checkCart(@PathVariable("skuId") Long skuId,
                       @RequestParam("isChecked") Integer isChecked) {
        cartService.checkCart(skuId, isChecked);
        return R.ok();
    }

    /**
     * 修改商品数量
     * PUT /api/cart/count/{skuId}
     */
    @PutMapping("/count/{skuId}")
    public R changeItemCount(@PathVariable("skuId") Long skuId,
                             @RequestParam("num") Integer num) {
        cartService.changeItemCount(skuId, num);
        return R.ok();
    }

    /**
     * 删除商品
     * DELETE /api/cart/item/{skuId}
     */
    @DeleteMapping("/item/{skuId}")
    public R deleteItem(@PathVariable("skuId") Long skuId) {
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
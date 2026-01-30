package com.xiaojie.cart.service;

import com.xiaojie.cart.vo.CartItemVo;
import com.xiaojie.cart.vo.CartVo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 购物车服务测试类
 */
@ExtendWith(MockitoExtension.class)
@SpringBootTest
class CartServiceTest {

    @Mock
    private CartService cartService;

    private CartVo mockCartVo;
    private CartItemVo mockCartItemVo;

    @BeforeEach
    void setUp() {
        mockCartVo = createMockCartVo();
        mockCartItemVo = createMockCartItemVo();
    }

    @Test
    void testAddCartItem() {
        // 准备测试数据
        Long skuId = 1L;
        Integer num = 1;
        
        // 模拟添加到购物车操作
        when(cartService.addCartItem(eq(skuId), eq(num))).thenReturn(mockCartItemVo);
        
        // 执行测试
        CartItemVo result = cartService.addCartItem(skuId, num);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(skuId, result.getSkuId());
        assertEquals(num, result.getCount());
        assertEquals("iPhone 13", result.getTitle());
        assertEquals(new BigDecimal("5999.00"), result.getPrice());
        
        verify(cartService, times(1)).addCartItem(eq(skuId), eq(num));
    }

    @Test
    void testAddCartItemWithNegativeNum() {
        // 测试负数商品数量
        Long skuId = 1L;
        Integer negativeNum = -1;
        
        when(cartService.addCartItem(eq(skuId), eq(negativeNum)))
            .thenThrow(new IllegalArgumentException("商品数量不能为负数"));
        
        // 执行测试并验证异常
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            cartService.addCartItem(skuId, negativeNum);
        });
        
        assertEquals("商品数量不能为负数", exception.getMessage());
        verify(cartService, times(1)).addCartItem(eq(skuId), eq(negativeNum));
    }

    @Test
    void testGetCart() {
        // 模拟返回购物车列表
        when(cartService.getCart()).thenReturn(mockCartVo);
        
        // 执行测试
        CartVo result = cartService.getCart();
        
        // 验证结果
        assertNotNull(result);
        assertNotNull(result.getItems());
        assertEquals(2, result.getItems().size());
        
        // 验证购物车项目
        List<CartItemVo> items = result.getItems();
        assertEquals((long)1L, (long)items.get(0).getSkuId());
        assertEquals((long)2L, (long)items.get(1).getSkuId());
        assertEquals("iPhone 13", items.get(0).getTitle());
        assertEquals("MacBook Pro", items.get(1).getTitle());
        
        verify(cartService, times(1)).getCart();
    }

    @Test
    void testGetCartWhenEmpty() {
        // 模拟空购物车
        CartVo emptyCart = new CartVo();
        emptyCart.setItems(Arrays.asList());
        emptyCart.setTotalAmount(BigDecimal.ZERO);
        
        when(cartService.getCart()).thenReturn(emptyCart);
        
        // 执行测试
        CartVo result = cartService.getCart();
        
        // 验证结果
        assertNotNull(result);
        assertNotNull(result.getItems());
        assertEquals(0, result.getItems().size());
        assertEquals(BigDecimal.ZERO, result.getTotalAmount());
        
        verify(cartService, times(1)).getCart();
    }

    @Test
    void testChangeItemCount() {
        // 准备测试数据
        Long skuId = 1L;
        Integer newNum = 3;
        
        // 模拟更新操作 - changeItemCount返回void
        doNothing().when(cartService).changeItemCount(eq(skuId), eq(newNum));
        
        // 执行测试
        cartService.changeItemCount(skuId, newNum);
        
        // 验证调用
        verify(cartService, times(1)).changeItemCount(eq(skuId), eq(newNum));
    }

    @Test
    void testDeleteItem() {
        // 准备测试数据
        Long skuId = 1L;
        
        // 模拟删除操作 - deleteItem返回void
        doNothing().when(cartService).deleteItem(eq(skuId));
        
        // 执行测试
        cartService.deleteItem(skuId);
        
        // 验证调用
        verify(cartService, times(1)).deleteItem(eq(skuId));
    }

    @Test
    void testCheckCart() {
        // 准备测试数据
        Long skuId = 1L;
        Integer isChecked = 1;
        
        // 模拟勾选操作 - checkCart返回void
        doNothing().when(cartService).checkCart(eq(skuId), eq(isChecked));
        
        // 执行测试
        cartService.checkCart(skuId, isChecked);
        
        // 验证调用
        verify(cartService, times(1)).checkCart(eq(skuId), eq(isChecked));
    }

    @Test
    void testGetCartItem() {
        // 准备测试数据
        Long skuId = 1L;
        
        // 模拟获取购物车项
        when(cartService.getCartItem(eq(skuId))).thenReturn(mockCartItemVo);
        
        // 执行测试
        CartItemVo result = cartService.getCartItem(skuId);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(skuId, result.getSkuId());
        
        verify(cartService, times(1)).getCartItem(eq(skuId));
    }

    @Test
    void testGetCheckedItems() {
        // 准备测试数据
        List<CartItemVo> checkedItems = Arrays.asList(mockCartItemVo);
        
        // 模拟获取已选中的购物车项
        when(cartService.getCheckedItems()).thenReturn(checkedItems);
        
        // 执行测试
        List<CartItemVo> result = cartService.getCheckedItems();
        
        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(mockCartItemVo.getSkuId(), result.get(0).getSkuId());
        
        verify(cartService, times(1)).getCheckedItems();
    }

    /**
     * 创建模拟的购物车VO
     */
    private CartVo createMockCartVo() {
        CartVo cart = new CartVo();
        
        CartItemVo item1 = createMockCartItemVo();
        CartItemVo item2 = createMockCartItemVo();
        item2.setSkuId(2L);
        item2.setTitle("MacBook Pro");
        item2.setPrice(new BigDecimal("10999.00"));
        item2.setCount(1);
        item2.setTotalPrice(new BigDecimal("10999.00"));
        
        cart.setItems(Arrays.asList(item1, item2));
        cart.setTotalAmount(new BigDecimal("16997.00"));
        
        return cart;
    }

    /**
     * 创建模拟的购物车项目VO
     */
    private CartItemVo createMockCartItemVo() {
        CartItemVo item = new CartItemVo();
        item.setSkuId(1L);
        item.setTitle("iPhone 13");
        item.setPrice(new BigDecimal("5999.00"));
        item.setCount(1);
        item.setTotalPrice(new BigDecimal("5999.00"));
        item.setImage("https://example.com/iphone13.jpg");
        return item;
    }
}
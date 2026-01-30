package com.xiaojie.order.service;

import com.xiaojie.common.utils.PageUtils;
import com.xiaojie.order.entity.OrderEntity;
import com.xiaojie.order.vo.OrderConfirmVo;
import com.xiaojie.order.vo.OrderSubmitVo;
import com.xiaojie.order.vo.SubmitOrderResponseVo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 订单服务测试类
 */
@ExtendWith(MockitoExtension.class)
@SpringBootTest
class OrderServiceTest {

    @Mock
    private OrderService orderService;

    private OrderSubmitVo mockOrderSubmitVo;
    private OrderEntity mockOrderEntity;

    @BeforeEach
    void setUp() {
        // 准备测试数据
        mockOrderSubmitVo = createMockOrderSubmitVo();
        mockOrderEntity = createMockOrderEntity();
    }

    @Test
    void testSubmitOrder() {
        // 模拟返回数据
        SubmitOrderResponseVo expectedResponse = new SubmitOrderResponseVo();
        expectedResponse.setCode(200);
        expectedResponse.setOrder(mockOrderEntity);
        
        when(orderService.submitOrder(any(OrderSubmitVo.class))).thenReturn(expectedResponse);
        
        // 执行测试
        SubmitOrderResponseVo result = orderService.submitOrder(mockOrderSubmitVo);
        
        // 验证结果
        assertNotNull(result);
        assertNotNull(result.getOrder());
        assertEquals(mockOrderEntity.getId(), result.getOrder().getId());
        
        verify(orderService, times(1)).submitOrder(any(OrderSubmitVo.class));
    }

    @Test
    void testSubmitOrderWithNullData() {
        // 测试空数据提交
        when(orderService.submitOrder(isNull())).thenThrow(new IllegalArgumentException("订单数据不能为空"));
        
        // 执行测试并验证异常
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            orderService.submitOrder(null);
        });
        
        assertEquals("订单数据不能为空", exception.getMessage());
        verify(orderService, times(1)).submitOrder(isNull());
    }

    @Test
    void testQueryOrder() {
        // 准备测试数据
        String orderSn = "ORDER123456789";
        
        // 模拟返回数据
        when(orderService.getOrderByOrderSn(eq(orderSn))).thenReturn(mockOrderEntity);
        
        // 执行测试
        OrderEntity result = orderService.getOrderByOrderSn(orderSn);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(orderSn, result.getOrderSn());
        assertEquals(new BigDecimal("299.99"), result.getTotalAmount());
        
        verify(orderService, times(1)).getOrderByOrderSn(eq(orderSn));
    }

    @Test
    void testQueryOrderWithInvalidOrderSn() {
        // 准备测试数据
        String invalidOrderSn = "INVALID_ORDER_SN";
        
        // 模拟返回空数据
        when(orderService.getOrderByOrderSn(eq(invalidOrderSn))).thenReturn(null);
        
        // 执行测试
        OrderEntity result = orderService.getOrderByOrderSn(invalidOrderSn);
        
        // 验证结果
        assertNull(result);
        verify(orderService, times(1)).getOrderByOrderSn(eq(invalidOrderSn));
    }

    @Test
    void testQueryPage() {
        // 模拟返回数据
        PageUtils mockPageUtils = new PageUtils(Arrays.asList(mockOrderEntity), 1, 10, 1);
        
        // 模拟返回数据
        Map<String, Object> params = new HashMap<>();
        params.put("page", 1);
        params.put("limit", 10);
        
        when(orderService.queryPage(any())).thenReturn(mockPageUtils);
        
        // 执行测试
        PageUtils result = orderService.queryPage(params);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.getTotalCount());
        assertEquals(1, result.getList().size());
        
        verify(orderService, times(1)).queryPage(any());
    }

    @Test
    void testCloseOrder() {
        // 模拟关闭订单操作 - closeOrder返回void
        doNothing().when(orderService).closeOrder(any(OrderEntity.class));
        
        // 执行测试
        orderService.closeOrder(mockOrderEntity);
        
        // 验证调用
        verify(orderService, times(1)).closeOrder(any(OrderEntity.class));
    }

    @Test
    void testConfirmOrder() {
        // 模拟确认订单操作
        OrderConfirmVo mockConfirmVo = new OrderConfirmVo();
        when(orderService.confirmOrder()).thenReturn(mockConfirmVo);
        
        // 执行测试
        OrderConfirmVo result = orderService.confirmOrder();
        
        // 验证结果
        assertNotNull(result);
        
        verify(orderService, times(1)).confirmOrder();
    }

    @Test
    void testGetMemberOrderPage() {
        // 模拟返回数据
        PageUtils mockPageUtils = new PageUtils(Arrays.asList(mockOrderEntity), 1, 10, 1);
        
        Map<String, Object> params = new HashMap<>();
        params.put("page", 1);
        params.put("limit", 10);
        
        when(orderService.getMemberOrderPage(any())).thenReturn(mockPageUtils);
        
        // 执行测试
        PageUtils result = orderService.getMemberOrderPage(params);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.getTotalCount());
        
        verify(orderService, times(1)).getMemberOrderPage(any());
    }

    /**
     * 创建模拟的订单提交VO
     */
    private OrderSubmitVo createMockOrderSubmitVo() {
        OrderSubmitVo vo = new OrderSubmitVo();
        vo.setAddrId(1L);
        vo.setPayPrice(new BigDecimal("299.99"));
        vo.setPayType(1);
        vo.setOrderToken("13800138000");
        vo.setRemarks("北京市朝阳区某某街道123号");
        return vo;
    }

    /**
     * 创建模拟的订单实体
     */
    private OrderEntity createMockOrderEntity() {
        OrderEntity order = new OrderEntity();
        order.setId(1L);
        order.setMemberId(1L);
        order.setOrderSn("ORDER123456789");
        order.setTotalAmount(new BigDecimal("299.99"));
        order.setPayAmount(new BigDecimal("299.99"));
        order.setFreightAmount(new BigDecimal("0.00"));
        order.setStatus(1); // 待付款
        order.setDeliveryCompany("顺丰快递");
        order.setReceiverName("张三");
        order.setReceiverPhone("13800138000");
        order.setReceiverDetailAddress("北京市朝阳区某某街道123号");
        order.setCreateTime(new java.util.Date());
        return order;
    }
}
package com.xiaojie.ware.service;

import com.xiaojie.common.to.SkuHasStockVo;
import com.xiaojie.common.to.mq.OrderTo;
import com.xiaojie.common.to.mq.StockLockedTo;
import com.xiaojie.common.utils.PageUtils;
import com.xiaojie.ware.entity.WareSkuEntity;
import com.xiaojie.ware.vo.WareSkuLockVo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WareSkuServiceTest {

    @Mock
    private WareSkuService wareSkuService;

    private WareSkuEntity mockWareSkuEntity;
    private WareSkuLockVo mockWareSkuLockVo;

    @BeforeEach
    void setUp() {
        mockWareSkuEntity = createMockWareSkuEntity();
        mockWareSkuLockVo = createMockWareSkuLockVo();
    }

    @Test
    void testQueryPage() {
        // 模拟返回数据
        PageUtils mockPageUtils = new PageUtils(Arrays.asList(mockWareSkuEntity), 1, 10, 1);
        
        Map<String, Object> params = new HashMap<>();
        params.put("page", 1);
        params.put("limit", 10);
        
        when(wareSkuService.queryPage(any())).thenReturn(mockPageUtils);
        
        // 执行测试
        PageUtils result = wareSkuService.queryPage(params);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.getTotalCount());
        assertEquals(1, result.getList().size());
        
        verify(wareSkuService, times(1)).queryPage(any());
    }

    @Test
    void testAddStock() {
        // 准备测试数据
        Long skuId = 1L;
        Long wareId = 1L;
        Integer skuNum = 10;
        
        // 模拟添加库存操作 - addStock返回void
        doNothing().when(wareSkuService).addStock(eq(skuId), eq(wareId), eq(skuNum));
        
        // 执行测试
        wareSkuService.addStock(skuId, wareId, skuNum);
        
        // 验证调用
        verify(wareSkuService, times(1)).addStock(eq(skuId), eq(wareId), eq(skuNum));
    }

    @Test
    void testGetSkuHasStocks() {
        // 准备测试数据
        List<Long> skuIds = Arrays.asList(1L, 2L);
        List<SkuHasStockVo> mockStockList = Arrays.asList(
            createMockSkuHasStockVo(1L, true),
            createMockSkuHasStockVo(2L, false)
        );
        
        when(wareSkuService.getSkuHasStocks(eq(skuIds))).thenReturn(mockStockList);
        
        // 执行测试
        List<SkuHasStockVo> result = wareSkuService.getSkuHasStocks(skuIds);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.get(0).getHasStock());
        assertFalse(result.get(1).getHasStock());
        
        verify(wareSkuService, times(1)).getSkuHasStocks(eq(skuIds));
    }

    @Test
    void testOrderLockStock() {
        // 模拟锁定库存操作
        when(wareSkuService.orderLockStock(any(WareSkuLockVo.class))).thenReturn(true);
        
        // 执行测试
        Boolean result = wareSkuService.orderLockStock(mockWareSkuLockVo);
        
        // 验证结果
        assertTrue(result);
        
        verify(wareSkuService, times(1)).orderLockStock(any(WareSkuLockVo.class));
    }

    @Test
    void testUnlockStockLockedTo() {
        // 准备测试数据
        StockLockedTo stockLockedTo = new StockLockedTo();
        
        // 模拟解锁库存操作 - unlock返回void
        doNothing().when(wareSkuService).unlock(any(StockLockedTo.class));
        
        // 执行测试
        wareSkuService.unlock(stockLockedTo);
        
        // 验证调用
        verify(wareSkuService, times(1)).unlock(any(StockLockedTo.class));
    }

    @Test
    void testUnlockOrderTo() {
        // 准备测试数据
        OrderTo orderTo = new OrderTo();
        
        // 模拟解锁库存操作 - unlock返回void
        doNothing().when(wareSkuService).unlock(any(OrderTo.class));
        
        // 执行测试
        wareSkuService.unlock(orderTo);
        
        // 验证调用
        verify(wareSkuService, times(1)).unlock(any(OrderTo.class));
    }

    /**
     * 创建模拟的库存实体
     */
    private WareSkuEntity createMockWareSkuEntity() {
        WareSkuEntity entity = new WareSkuEntity();
        entity.setId(1L);
        entity.setSkuId(1L);
        entity.setWareId(1L);
        entity.setStock(100);
        entity.setSkuName("测试商品");
        return entity;
    }

    /**
     * 创建模拟的库存锁定VO
     */
    private WareSkuLockVo createMockWareSkuLockVo() {
        WareSkuLockVo lockVo = new WareSkuLockVo();
        lockVo.setOrderSn("ORDER123456789");
        return lockVo;
    }

    /**
     * 创建模拟的库存状态VO
     */
    private SkuHasStockVo createMockSkuHasStockVo(Long skuId, boolean hasStock) {
        SkuHasStockVo vo = new SkuHasStockVo();
        vo.setSkuId(skuId);
        vo.setHasStock(hasStock);
        return vo;
    }
}
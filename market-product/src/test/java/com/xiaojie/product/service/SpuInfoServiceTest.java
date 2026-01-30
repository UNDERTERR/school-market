package com.xiaojie.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaojie.common.utils.PageUtils;
import com.xiaojie.product.entity.SpuInfoEntity;
import com.xiaojie.product.vo.SpuSaveVo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpuInfoServiceTest {

    @Mock
    private SpuInfoService spuInfoService;

    private SpuInfoEntity mockSpuInfoEntity;
    private SpuSaveVo mockSpuSaveVo;

    @BeforeEach
    void setUp() {
        mockSpuInfoEntity = createMockSpuInfoEntity();
        mockSpuSaveVo = createMockSpuSaveVo();
    }

    @Test
    void testQueryPage() {
        // 模拟返回数据
        PageUtils mockPageUtils = new PageUtils(Arrays.asList(mockSpuInfoEntity), 1, 10, 1);
        
        Map<String, Object> params = new HashMap<>();
        params.put("page", 1);
        params.put("limit", 10);
        
        when(spuInfoService.queryPage(any())).thenReturn(mockPageUtils);
        
        // 执行测试
        PageUtils result = spuInfoService.queryPage(params);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.getTotalCount());
        assertEquals(1, result.getList().size());
        
        verify(spuInfoService, times(1)).queryPage(any());
    }

    @Test
    void testSaveSpuSaveVo() {
        // 模拟保存SPU操作 - saveSpuSaveVo返回void
        doNothing().when(spuInfoService).saveSpuSaveVo(any(SpuSaveVo.class));
        
        // 执行测试
        spuInfoService.saveSpuSaveVo(mockSpuSaveVo);
        
        // 验证调用
        verify(spuInfoService, times(1)).saveSpuSaveVo(any(SpuSaveVo.class));
    }

    @Test
    void testQueryPageByCondition() {
        // 模拟返回数据
        PageUtils mockPageUtils = new PageUtils(Arrays.asList(mockSpuInfoEntity), 1, 10, 1);
        
        Map<String, Object> params = new HashMap<>();
        params.put("page", 1);
        params.put("limit", 10);
        params.put("key", "测试");
        
        when(spuInfoService.queryPageByCondition(any())).thenReturn(mockPageUtils);
        
        // 执行测试
        PageUtils result = spuInfoService.queryPageByCondition(params);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.getTotalCount());
        assertEquals(1, result.getList().size());
        
        verify(spuInfoService, times(1)).queryPageByCondition(any());
    }

    @Test
    void testUpSpuForSearch() {
        // 准备测试数据
        Long spuId = 1L;
        
        // 模拟上架商品操作 - upSpuForSearch返回void
        doNothing().when(spuInfoService).upSpuForSearch(eq(spuId));
        
        // 执行测试
        spuInfoService.upSpuForSearch(spuId);
        
        // 验证调用
        verify(spuInfoService, times(1)).upSpuForSearch(eq(spuId));
    }

    @Test
    void testGetSpuBySkuId() {
        // 准备测试数据
        Long skuId = 1L;
        
        // 模拟获取SPU信息
        when(spuInfoService.getSpuBySkuId(eq(skuId))).thenReturn(mockSpuInfoEntity);
        
        // 执行测试
        SpuInfoEntity result = spuInfoService.getSpuBySkuId(skuId);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(skuId, result.getId());
        assertEquals("测试商品SPU", result.getSpuName());
        
        verify(spuInfoService, times(1)).getSpuBySkuId(eq(skuId));
    }

    /**
     * 创建模拟的SPU实体
     */
    private SpuInfoEntity createMockSpuInfoEntity() {
        SpuInfoEntity entity = new SpuInfoEntity();
        entity.setId(1L);
        entity.setSpuName("测试商品SPU");
        entity.setSpuDescription("这是一个测试商品");
        entity.setCatalogId(1L);
        entity.setBrandId(1L);
        entity.setPublishStatus(1);
        entity.setCreateTime(new java.util.Date());
        entity.setUpdateTime(new java.util.Date());
        return entity;
    }

    /**
     * 创建模拟的SPU保存VO
     */
    private SpuSaveVo createMockSpuSaveVo() {
        SpuSaveVo vo = new SpuSaveVo();
        vo.setSpuName("测试商品SPU");
        vo.setSpuDescription("这是一个测试商品");
        vo.setCatalogId(1L);
        vo.setBrandId(1L);
        return vo;
    }
}
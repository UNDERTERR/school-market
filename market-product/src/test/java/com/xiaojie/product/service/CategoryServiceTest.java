package com.xiaojie.product.service;

import com.xiaojie.product.entity.CategoryEntity;
import com.xiaojie.product.vo.Catalog2Vo;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 商品分类服务测试类
 */
@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class CategoryServiceTest {

    @Mock
    private CategoryService categoryService;

    @Test
    public void testListWithTree() {
        // 模拟返回数据
        List<CategoryEntity> mockCategories = Arrays.asList(
                createMockCategory(1L, "图书", 0L, 1),
                createMockCategory(2L, "手机", 0L, 1)
        );

        when(categoryService.listWithTree()).thenReturn(mockCategories);

        // 执行测试
        List<CategoryEntity> result = categoryService.listWithTree();

        // 验证结果
        assertNotNull("测试结果不能为空", result);
        assertEquals("期望返回2个分类", 2, result.size());
        assertEquals("第一个分类应该是图书", "图书", result.get(0).getName());
        assertEquals("第二个分类应该是手机", "手机", result.get(1).getName());

        verify(categoryService, times(1)).listWithTree();
    }

    @Test
    public void testFindCatelogPathById() {
        // 准备测试数据
        Long categoryId = 1L;
        Long[] expectedPath = {0L, 1L};

        // 模拟返回数据
        when(categoryService.findCatelogPathById(categoryId)).thenReturn(expectedPath);

        // 执行测试
        Long[] result = categoryService.findCatelogPathById(categoryId);

        // 验证结果
        assertNotNull("路径数组不能为空", result);
        assertArrayEquals("分类路径不匹配", expectedPath, result);
        assertEquals("路径长度应该是2", 2, result.length);

        verify(categoryService, times(1)).findCatelogPathById(categoryId);
    }

    @Test
    public void testGetLevel1Catagories() {
        // 模拟返回数据
        List<CategoryEntity> mockCategories = Arrays.asList(
                createMockCategory(1L, "图书", 0L, 1),
                createMockCategory(2L, "手机", 0L, 1),
                createMockCategory(3L, "家电", 0L, 1)
        );

        when(categoryService.getLevel1Catagories()).thenReturn(mockCategories);

        // 执行测试
        List<CategoryEntity> result = categoryService.getLevel1Catagories();

        // 验证结果
        assertNotNull("分类列表不能为空", result);
        assertEquals("应该返回3个分类", 3, result.size());

        // 验证都是一级分类
        for (CategoryEntity category : result) {
            assertEquals("第" + category.getCatId() + "个分类应该是一级分类", 0L, category.getParentCid().longValue());
            assertEquals("第" + category.getCatId() + "个分类层级应该是1", 1, category.getCatLevel().intValue());
        }

        verify(categoryService, times(1)).getLevel1Catagories();
    }

    @Test
    public void testGetCatelogJsonDbWithRedisson() {
        // 模拟返回数据
        Map<String, List<Catalog2Vo>> mockCatalogJson = new HashMap<>();
        // 这里需要创建Catalog2Vo对象而不是Map
        Catalog2Vo catalog2Vo1 = new Catalog2Vo();
        catalog2Vo1.setName("小说");
        Catalog2Vo catalog2Vo2 = new Catalog2Vo();
        catalog2Vo2.setName("科技");

        mockCatalogJson.put("1", Arrays.asList(catalog2Vo1, catalog2Vo2));
        mockCatalogJson.put("2", Arrays.asList(catalog2Vo1, catalog2Vo2));

        when(categoryService.getCatelogJsonDbWithRedisson()).thenReturn(mockCatalogJson);

        // 执行测试
        Map<String, List<Catalog2Vo>> result = categoryService.getCatelogJsonDbWithRedisson();

        // 验证结果
        assertNotNull("分类数据不能为空", result);
        assertTrue("应该包含一级分类", result.containsKey("1"));
        assertTrue("应该包含二级分类", result.containsKey("2"));
        assertEquals("应该有2个分类", 2, result.size());

        verify(categoryService, times(1)).getCatelogJsonDbWithRedisson();
    }


    @Test
    public void testRemoveMenuByIds() {
        // 准备测试数据
        List<Long> idsToDelete = Arrays.asList(1L, 2L, 3L);

        // 模拟删除操作
        doNothing().when(categoryService).removeMenuByIds(any());

        Assertions.assertDoesNotThrow(() ->
                        categoryService.removeMenuByIds(idsToDelete)
                ,
                "删除操作不应抛出异常"
        );

        verify(categoryService, times(1)).removeMenuByIds(idsToDelete);
    }

    /**
     * 创建模拟的分类实体
     */
    private CategoryEntity createMockCategory(Long catId, String name, Long parentCid, Integer catLevel) {
        CategoryEntity category = new CategoryEntity();
        category.setCatId(catId);
        category.setName(name);
        category.setParentCid(parentCid);
        category.setCatLevel(catLevel);
        category.setShowStatus(1);
        category.setSort(0);
        return category;
    }
}
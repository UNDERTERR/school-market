package com.xiaojie.product.controller;

import com.xiaojie.product.config.IntegrationTest;
import com.xiaojie.product.entity.CategoryEntity;
import com.xiaojie.product.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 商品分类控制器集成测试
 */
@IntegrationTest
class CategoryControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private CategoryService categoryService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void testGetLevel1Categories() throws Exception {
        // 准备测试数据
        List<CategoryEntity> categories = Arrays.asList(
            createMockCategory(1L, "图书", 0L, 1),
            createMockCategory(2L, "手机", 0L, 1),
            createMockCategory(3L, "家电", 0L, 1)
        );

        // 模拟服务返回
        when(categoryService.getLevel1Catagories()).thenReturn(categories);

        // 执行测试
        mockMvc.perform(get("/product/category/level1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(3))
                .andExpect(jsonPath("$.data[0].catId").value(1))
                .andExpect(jsonPath("$.data[0].name").value("图书"));

        // 验证服务调用
        verify(categoryService, times(1)).getLevel1Catagories();
    }

    @Test
    void testListWithTree() throws Exception {
        // 准备测试数据
        List<CategoryEntity> categories = Arrays.asList(
            createMockCategory(1L, "图书", 0L, 1),
            createMockCategory(11L, "小说", 1L, 2),
            createMockCategory(12L, "科技", 1L, 2)
        );

        // 模拟服务返回
        when(categoryService.listWithTree()).thenReturn(categories);

        // 执行测试
        mockMvc.perform(get("/product/category/list/tree"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(3));

        // 验证服务调用
        verify(categoryService, times(1)).listWithTree();
    }

    @Test
    void testFindCatelogPath() throws Exception {
        // 准备测试数据
        Long categoryId = 1L;
        Long[] expectedPath = {0L, 1L};

        // 模拟服务返回
        when(categoryService.findCatelogPathById(categoryId)).thenReturn(expectedPath);

        // 执行测试
        mockMvc.perform(get("/product/category/path/{categoryId}", categoryId))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2));

        // 验证服务调用
        verify(categoryService, times(1)).findCatelogPathById(categoryId);
    }

    @Test
    void testDeleteCategory() throws Exception {
        // 准备测试数据
        List<Long> ids = Arrays.asList(1L, 2L, 3L);

        // 模拟服务调用
        doNothing().when(categoryService).removeMenuByIds(ids);

        // 执行测试
        mockMvc.perform(delete("/product/category/delete")
                .contentType("application/json")
                .content("[1,2,3]"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.code").value(0));

        // 验证服务调用
        verify(categoryService, times(1)).removeMenuByIds(ids);
    }

    @Test
    void testUpdateCategory() throws Exception {
        // 准备测试数据
        CategoryEntity category = createMockCategory(1L, "更新后的分类", 0L, 1);

        // 模拟服务调用
        doNothing().when(categoryService).updateCascade(any(CategoryEntity.class));

        // 执行测试
        mockMvc.perform(put("/product/category/update")
                .contentType("application/json")
                .content("{\"catId\":1,\"name\":\"更新后的分类\",\"parentCid\":0,\"catLevel\":1,\"showStatus\":1,\"sort\":0}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.code").value(0));

        // 验证服务调用
        verify(categoryService, times(1)).updateCascade(any(CategoryEntity.class));
    }

    @Test
    void testSaveCategory() throws Exception {
        // 模拟服务调用
        doNothing().when(categoryService).save(any(CategoryEntity.class));

        // 执行测试
        mockMvc.perform(post("/product/category/save")
                .contentType("application/json")
                .content("{\"name\":\"新分类\",\"parentCid\":0,\"catLevel\":1,\"showStatus\":1,\"sort\":0}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.code").value(0));

        // 验证服务调用
        verify(categoryService, times(1)).save(any(CategoryEntity.class));
    }

    @Test
    void testGetCatalogJson() throws Exception {
        // 模拟服务返回
        when(categoryService.getCatelogJsonDbWithRedisson()).thenReturn(new HashMap<>());

        // 执行测试
        mockMvc.perform(get("/product/category/catalog/json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.code").value(0));

        // 验证服务调用
        verify(categoryService, times(1)).getCatelogJsonDbWithRedisson();
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
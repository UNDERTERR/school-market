package com.xiaojie.product.controller;


import com.xiaojie.common.utils.PageUtils;
import com.xiaojie.common.utils.R;
import com.xiaojie.product.entity.CategoryEntity;
import com.xiaojie.product.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("product/category")
@Tag(name = "商品分类管理", description = "商品分类的增删改查接口")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

/**
     * 查询所有分类并将查到分类通过树状结构组装起来
     */
    @GetMapping("/list/tree")
    @Operation(summary = "获取分类树", description = "查询所有分类并以树状结构返回")
    public List<CategoryEntity> list(){
        List<CategoryEntity> categoryEntities = categoryService.listWithTree();

        return categoryEntities;
    }

/**
     * 列表
     */
    @GetMapping("/list")
    @Operation(summary = "分页查询分类", description = "根据条件分页查询分类列表")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = categoryService.queryPage(params);

        return R.ok().put("page", page);
    }


/**
     * 信息
     */
    @GetMapping("/info/{catId}")
    @Operation(summary = "获取分类详情", description = "根据分类ID获取分类详细信息")
    public R info(@Parameter(description = "分类ID") @PathVariable("catId") Long catId){
        CategoryEntity category = categoryService.getById(catId);

        return R.ok().put("category", category);
    }

/**
     * 保存
     */
    @PostMapping("/save")
    @Operation(summary = "保存分类", description = "新增商品分类")
    public R save(@Parameter(description = "分类信息") @RequestBody CategoryEntity category){
        categoryService.save(category);

        return R.ok();
    }

/**
     * 修改
     * 级联更新所有数据
     */
    @PutMapping("/update")
    @Operation(summary = "更新分类", description = "级联更新分类及其关联数据")
    public R update(@Parameter(description = "分类信息") @RequestBody CategoryEntity category){
        categoryService.updateCascade(category);
        return R.ok();
    }

/**
     * 批量修改
     * @param categorys
     * @return
     */
    @PutMapping("/updateNodes")
    @Operation(summary = "批量更新分类", description = "批量更新多个分类信息")
    public R update(@Parameter(description = "分类信息数组") @RequestBody CategoryEntity[] categorys){
        categoryService.updateBatchById(Arrays.asList(categorys));
        return R.ok();
    }

/**
     * 删除
     */
    @DeleteMapping("/delete")
    @Operation(summary = "删除分类", description = "根据分类ID删除分类")
    public R delete(@Parameter(description = "分类ID数组") @RequestBody Long[] catIds){

        categoryService.removeMenuByIds(Arrays.asList(catIds));
        return R.ok();
    }
}

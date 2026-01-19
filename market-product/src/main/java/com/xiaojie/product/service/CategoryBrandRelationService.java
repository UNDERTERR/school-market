package com.xiaojie.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaojie.common.utils.PageUtils;
import com.xiaojie.product.entity.AttrAttrgroupRelationEntity;
import com.xiaojie.product.entity.CategoryBrandRelationEntity;
import com.xiaojie.product.entity.CategoryEntity;

import java.util.List;
import java.util.Map;


/**
 * 品牌分类关联
 */
public interface CategoryBrandRelationService extends IService<CategoryBrandRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveDetail(CategoryBrandRelationEntity categoryBrandRelation);

    void updateCategory(CategoryEntity category);

    void updateBrand(Long brandId, String name);

    List<CategoryBrandRelationEntity> getBrandsByCatId(Long catelogId);
}

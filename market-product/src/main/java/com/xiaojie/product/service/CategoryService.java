package com.xiaojie.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaojie.common.utils.PageUtils;
import com.xiaojie.product.entity.AttrAttrgroupRelationEntity;
import com.xiaojie.product.entity.CategoryEntity;
import com.xiaojie.product.vo.Catalog2Vo;

import java.util.List;
import java.util.Map;

public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<CategoryEntity> listWithTree();

    void removeMenuByIds(List<Long> asList);

    /**
     * 找到该三级分类的完整路径
     *
     * @param categorygId
     * @return
     */
    Long[] findCatelogPathById(Long categorygId);

    void updateCascade(CategoryEntity category);

    List<CategoryEntity> getLevel1Catagories();

    Map<String, List<Catalog2Vo>> getCatelogJsonDbWithRedisson();

}

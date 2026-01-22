package com.xiaojie.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaojie.common.utils.PageUtils;
import com.xiaojie.common.utils.Query;
import com.xiaojie.product.dao.BrandDao;
import com.xiaojie.product.dao.CategoryBrandRelationDao;
import com.xiaojie.product.dao.CategoryDao;
import com.xiaojie.product.entity.BrandEntity;
import com.xiaojie.product.entity.CategoryBrandRelationEntity;
import com.xiaojie.product.entity.CategoryEntity;
import com.xiaojie.product.service.CategoryBrandRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service("categoryBrandRelationService")
public class CategoryBrandRelationServiceImpl extends ServiceImpl<CategoryBrandRelationDao, CategoryBrandRelationEntity> implements CategoryBrandRelationService {


    @Autowired
    private BrandDao brandDao;

    @Autowired
    private CategoryDao categoryDao;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {

        IPage<CategoryBrandRelationEntity> page = this.page(
                new Query<CategoryBrandRelationEntity>().getPage(params),
                new QueryWrapper<CategoryBrandRelationEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveDetail(CategoryBrandRelationEntity categoryBrandRelation) {
        Long brandId = categoryBrandRelation.getBrandId();
        Long catelogId = categoryBrandRelation.getCatelogId();

        BrandEntity brandEntity = brandDao.selectById(brandId);
        CategoryEntity categoryEntity = categoryDao.selectById(catelogId);

        categoryBrandRelation.setBrandName(brandEntity.getName());
        categoryBrandRelation.setCatelogName(categoryEntity.getName());
        this.save(categoryBrandRelation);
    }

    @Override
    public void updateCategory(CategoryEntity category) {
        CategoryBrandRelationEntity categoryBrandRelationEntity = new CategoryBrandRelationEntity();
        categoryBrandRelationEntity.setCatelogName(category.getName());
        categoryBrandRelationEntity.setCatelogId(category.getCatId());

        this.update(categoryBrandRelationEntity,
                new UpdateWrapper<CategoryBrandRelationEntity>().eq("catelog_id", category.getCatId()));
    }

    @Override
    public void updateBrand(Long brandId, String name) {
        baseMapper.updateBrand(brandId, name);
    }

    @Override
    public List<CategoryBrandRelationEntity> getBrandsByCatId(Long catelogId) {
        List<CategoryBrandRelationEntity> entities = baseMapper.selectList(
                new QueryWrapper<CategoryBrandRelationEntity>().eq("catelog_id", catelogId));
        return entities;
    }
}

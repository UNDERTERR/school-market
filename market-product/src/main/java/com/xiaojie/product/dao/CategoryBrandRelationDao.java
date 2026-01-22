package com.xiaojie.product.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaojie.product.entity.CategoryBrandRelationEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 品牌分类关联
 *
 */
@Mapper
public interface CategoryBrandRelationDao extends BaseMapper<CategoryBrandRelationEntity> {


    @Update("UPDATE `pms_category_brand_relation` set brand_name=#{name} where brand_id=#{brandId}")
    void updateBrand(@Param("brandId") Long brandId, @Param("name") String name);
}

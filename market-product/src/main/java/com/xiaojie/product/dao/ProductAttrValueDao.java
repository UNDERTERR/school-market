package com.xiaojie.product.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaojie.product.entity.ProductAttrValueEntity;
import com.xiaojie.product.vo.SpuItemAttrGroupVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * spu属性值
 * 

 */
@Mapper
public interface ProductAttrValueDao extends BaseMapper<ProductAttrValueEntity> {


    @Select("SELECT ag.attr_group_name,attr.attr_id,attr.attr_name,attr.attr_value" +
            "FROM pms_attr_attrgroup_relation aar " +
            "LEFT JOIN pms_attr_group ag ON aar.attr_group_id=ag.attr_group_id" +
            "LEFT JOIN pms_product_attr_value attr ON aar.attr_id=attr.attr_id" +
            "WHERE attr.spu_id = #{spuId} AND ag.catelog_id = #{catalogId}")
    List<SpuItemAttrGroupVo> getProductGroupAttrsBySpuId(@Param("spuId") Long spuId, @Param("catalogId") Long catalogId);
}

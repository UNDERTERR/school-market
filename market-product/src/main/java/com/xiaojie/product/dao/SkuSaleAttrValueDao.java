package com.xiaojie.product.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaojie.product.entity.SkuSaleAttrValueEntity;
import com.xiaojie.product.vo.SkuItemSaleAttrVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * sku销售属性&值
 * 

 */
@Mapper
public interface SkuSaleAttrValueDao extends BaseMapper<SkuSaleAttrValueEntity> {


    @Select("SELECT attr_id,attr_name,attr_value,GROUP_CONCAT(info.sku_id) sku_ids FROM pms_sku_info info" +
            "LEFT JOIN pms_sku_sale_attr_value ssav ON info.sku_id=ssav.sku_id" +
            "WHERE info.spu_id= #{spuId}" +
            "GROUP BY ssav.attr_id,ssav.attr_name,ssav.attr_value")
    List<SkuItemSaleAttrVo> listSaleAttrs(@Param("spuId") Long spuId);


    @Select("SELECT attr_value FROM pms_sku_sale_attr_value WHERE sku_id=#{skuId}")
    List<String> getSkuSaleAttrValuesAsString(@Param("skuId") Long skuId);
}

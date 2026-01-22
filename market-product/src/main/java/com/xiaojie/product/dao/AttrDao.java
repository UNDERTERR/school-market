package com.xiaojie.product.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaojie.product.entity.AttrEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 商品属性
 * 

 */
@Mapper
public interface AttrDao extends BaseMapper<AttrEntity> {

    @Select("<script>" + "SELECT attr_id FROM pms_attr WHERE search_type = 1 AND attr_id IN" +
            "<foreach collection='attrIds' item='id' separator=',' open='(' close=')'>" + "#{id}" + "</foreach>" +
            "</script>")
    List<Long> selectSearchAttrIds(@Param("attrIds") List<Long> attrIds);
}

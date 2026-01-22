package com.xiaojie.ware.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaojie.ware.entity.WareSkuEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 商品库存
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {

    @Update(" UPDATE wms_ware_sku SET stock=stock+#{skuNum} WHERE skuId=#{skuId} AND wareId=#{skuId}")
    void addstock(@Param("skuId") Long skuId, @Param("wareId") Long wareId, @Param("skuNum") Integer skuNum);

    @Select("SELECT SUM(stock-stock_locked) FROM wms_ware_sku WHERE sku_id=#{id}")
    Integer getTotalStock(@Param("id") Long id);

    @Select("SELECT ware_id FROM wms_ware_sku WHERE sku_id=#{skuId} AND stock-stock_locked>=#{count}")
    List<Long> listWareIdsHasStock(@Param("skuId") Long skuId, @Param("count") Integer count);

    @Update("UPDATE wms_ware_sku SET stock_locked=stock_locked+#{num} WHERE sku_id=#{skuId} AND ware_id=#{wareId} AND stock-stock_locked>#{num}")
    Long lockWareSku(@Param("skuId") Long skuId, @Param("num") Integer num, @Param("wareId") Long wareId);

    @Update("UPDATE wms_ware_sku SET stock_locked=stock_locked-#{skuNum} WHERE sku_id=#{skuId} AND ware_id=#{wareId}")
    void unlockStock(@Param("skuId") Long skuId, @Param("skuNum") Integer skuNum, @Param("wareId") Long wareId);
}

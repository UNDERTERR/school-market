package com.xiaojie.product.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaojie.product.entity.SpuInfoEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * spu信息
 */
@Mapper
public interface SpuInfoDao extends BaseMapper<SpuInfoEntity> {


    @Update("update pms_spu_info set publish_status=#{code} where id=#{spuId}")
    void upSpuStatus(@Param("spuId") Long spuId, @Param("code") int code);
}

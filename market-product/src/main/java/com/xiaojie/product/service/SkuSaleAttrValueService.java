package com.xiaojie.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaojie.common.utils.PageUtils;
import com.xiaojie.product.entity.SkuSaleAttrValueEntity;
import com.xiaojie.product.vo.SkuItemSaleAttrVo;

import java.util.List;
import java.util.Map;

public interface SkuSaleAttrValueService extends IService<SkuSaleAttrValueEntity> {
    PageUtils queryPage(Map<String, Object> params);

    List<SkuItemSaleAttrVo> listSaleAttrs(Long spuId);

    List<String> getSkuSaleAttrValuesAsString(Long skuId);
}

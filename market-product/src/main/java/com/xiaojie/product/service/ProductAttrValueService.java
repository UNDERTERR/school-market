package com.xiaojie.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaojie.common.utils.PageUtils;
import com.xiaojie.product.entity.ProductAttrValueEntity;
import com.xiaojie.product.vo.SpuItemAttrGroupVo;

import java.util.List;
import java.util.Map;

/**
 * spu属性值
 */
public interface ProductAttrValueService extends IService<ProductAttrValueEntity> {
    PageUtils queryPage(Map<String, Object> params);

    List<SpuItemAttrGroupVo> getProductGroupAttrsBySpuId(Long spuId, Long catalogId);
}

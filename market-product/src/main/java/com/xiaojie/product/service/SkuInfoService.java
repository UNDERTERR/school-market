package com.xiaojie.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaojie.common.utils.PageUtils;
import com.xiaojie.product.entity.SkuInfoEntity;
import com.xiaojie.product.vo.SkuItemVo;

import java.util.List;
import java.util.Map;

public interface SkuInfoService extends IService<SkuInfoEntity> {
    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPageByCondition(Map<String, Object> params);

    List<SkuInfoEntity> getSkusBySpuId(Long spuId);

    SkuItemVo item(Long skuId);
}

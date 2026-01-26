package com.xiaojie.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaojie.common.to.SkuReductionTo;
import com.xiaojie.common.utils.PageUtils;
import com.xiaojie.coupon.entity.SkuFullReductionEntity;

import java.util.Map;

/**
 * 商品满减信息

 */
public interface SkuFullReductionService extends IService<SkuFullReductionEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSkuReductionTo(SkuReductionTo skuReductionTo);
}


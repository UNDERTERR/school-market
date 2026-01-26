package com.xiaojie.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaojie.common.utils.PageUtils;
import com.xiaojie.coupon.entity.SpuBoundsEntity;

import java.util.Map;

/**
 * 商品spu积分设置

 */
public interface SpuBoundsService extends IService<SpuBoundsEntity> {

    PageUtils queryPage(Map<String, Object> params);
}


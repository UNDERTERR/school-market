package com.xiaojie.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaojie.common.utils.PageUtils;
import com.xiaojie.coupon.entity.CouponEntity;

import java.util.Map;

/**
 * 优惠券信息

 */
public interface CouponService extends IService<CouponEntity> {

    PageUtils queryPage(Map<String, Object> params);
}


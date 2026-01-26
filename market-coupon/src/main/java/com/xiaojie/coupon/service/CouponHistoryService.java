package com.xiaojie.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaojie.common.utils.PageUtils;
import com.xiaojie.coupon.entity.CouponHistoryEntity;

import java.util.Map;

/**
 * 优惠券领取历史记录

 */
public interface CouponHistoryService extends IService<CouponHistoryEntity> {

    PageUtils queryPage(Map<String, Object> params);
}


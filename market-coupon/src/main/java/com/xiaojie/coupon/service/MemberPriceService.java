package com.xiaojie.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaojie.common.utils.PageUtils;
import com.xiaojie.coupon.entity.MemberPriceEntity;

import java.util.Map;

/**
 * 商品会员价格

 */
public interface MemberPriceService extends IService<MemberPriceEntity> {

    PageUtils queryPage(Map<String, Object> params);
}


package com.xiaojie.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaojie.common.utils.PageUtils;
import com.xiaojie.coupon.entity.SeckillSkuNoticeEntity;

import java.util.Map;

/**
 * 秒杀商品通知订阅

 */
public interface SeckillSkuNoticeService extends IService<SeckillSkuNoticeEntity> {

    PageUtils queryPage(Map<String, Object> params);
}


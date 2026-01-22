package com.xiaojie.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaojie.common.utils.PageUtils;
import com.xiaojie.ware.entity.PurchaseDetailEntity;

import java.util.Map;

/**
 * 
 *

 */
public interface PurchaseDetailService extends IService<PurchaseDetailEntity> {

    PageUtils queryPage(Map<String, Object> params);
}


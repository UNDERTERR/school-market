package com.xiaojie.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaojie.common.utils.PageUtils;
import com.xiaojie.order.entity.OrderOperateHistoryEntity;

import java.util.Map;

/**
 * 订单操作历史记录

 */
public interface OrderOperateHistoryService extends IService<OrderOperateHistoryEntity> {

    PageUtils queryPage(Map<String, Object> params);
}


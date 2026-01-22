package com.xiaojie.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaojie.common.to.SkuHasStockVo;
import com.xiaojie.common.to.mq.OrderTo;
import com.xiaojie.common.to.mq.StockLockedTo;
import com.xiaojie.common.utils.PageUtils;
import com.xiaojie.ware.entity.WareSkuEntity;
import com.xiaojie.ware.vo.WareSkuLockVo;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void addStock(Long skuId, Long wareId, Integer skuNum);

    List<SkuHasStockVo> getSkuHasStocks(List<Long> ids);

    Boolean orderLockStock(WareSkuLockVo lockVo);

    void unlock(StockLockedTo stockLockedTo);

    void unlock(OrderTo orderTo);
}


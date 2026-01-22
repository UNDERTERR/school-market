package com.xiaojie.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaojie.common.utils.PageUtils;
import com.xiaojie.ware.entity.PurchaseEntity;
import com.xiaojie.ware.vo.MergeVo;
import com.xiaojie.ware.vo.PurchaseDoneVo;

import java.util.List;
import java.util.Map;

/**
 * 采购信息
 *

 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils listUnreceive(Map<String, Object> params);

    void mergePurchaseDetail(MergeVo mergeVo);

    void ReceivedPurchase(List<Long> ids);

    void finishPurchase(PurchaseDoneVo purchaseDoneVo);
}


package com.xiaojie.ware.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaojie.common.utils.PageUtils;
import com.xiaojie.ware.dao.PurchaseDao;
import com.xiaojie.ware.entity.PurchaseEntity;
import com.xiaojie.ware.service.PurchaseService;
import com.xiaojie.ware.vo.MergeVo;
import com.xiaojie.ware.vo.PurchaseDoneVo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        return null;
    }

    @Override
    public PageUtils listUnreceive(Map<String, Object> params) {
        return null;
    }

    @Override
    public void mergePurchaseDetail(MergeVo mergeVo) {

    }

    @Override
    public void ReceivedPurchase(List<Long> ids) {

    }

    @Override
    public void finishPurchase(PurchaseDoneVo purchaseDoneVo) {

    }
}

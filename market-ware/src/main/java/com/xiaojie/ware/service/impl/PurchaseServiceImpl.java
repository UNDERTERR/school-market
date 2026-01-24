package com.xiaojie.ware.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaojie.common.constant.WareConstant;
import com.xiaojie.common.utils.PageUtils;
import com.xiaojie.common.utils.Query;
import com.xiaojie.ware.dao.PurchaseDao;
import com.xiaojie.ware.entity.PurchaseDetailEntity;
import com.xiaojie.ware.entity.PurchaseEntity;
import com.xiaojie.ware.service.PurchaseDetailService;
import com.xiaojie.ware.service.PurchaseService;
import com.xiaojie.ware.service.WareSkuService;
import com.xiaojie.ware.vo.MergeVo;
import com.xiaojie.ware.vo.PurchaseDoneVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {

    @Autowired
    private PurchaseDetailService purchaseDetailService;

    @Autowired
    private WareSkuService wareSkuService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils listUnreceive(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>().eq("status", 0).or().eq("status", 1));
        return new PageUtils(page);
    }

    @Override
    public void mergePurchaseDetail(MergeVo mergeVo) {
        Long purchaseId = mergeVo.getPurchaseId();
        if (purchaseId == null) {
            PurchaseEntity entity = new PurchaseEntity();
            entity.setCreateTime(new Date());
            entity.setUpdateTime(new Date());
            entity.setStatus(WareConstant.PurchaseStatusEnum.CREATED.getCode());
            this.save(entity);
            purchaseId = entity.getId();
        }

        List<Long> items = mergeVo.getItems();
        Long finalPurchaseId = purchaseId;
        List<PurchaseDetailEntity> collect = items.stream().map(item -> {
            PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
            purchaseDetailEntity.setId(item);
            purchaseDetailEntity.setPurchaseId(finalPurchaseId);
            purchaseDetailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.ASSIGNED.getCode());
            return purchaseDetailEntity;
        }).collect(Collectors.toList());

        purchaseDetailService.updateBatchById(collect);

        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(purchaseId);
        purchaseEntity.setUpdateTime(new Date());
        this.updateById(purchaseEntity);
    }


    @Transactional
    @Override
    public void ReceivedPurchase(List<Long> ids) {
        //更新采购单状态及更新时间
        List<PurchaseEntity> collect = ids.stream().map(id -> {
            PurchaseEntity byId = this.getById(id);
            return byId;
        }).filter(entity -> {
            return entity.getStatus() == WareConstant.PurchaseStatusEnum.CREATED.getCode() || entity.getStatus() == WareConstant.PurchaseStatusEnum.ASSIGNED.getCode();
        }).map(entity -> {
            entity.setStatus(WareConstant.PurchaseStatusEnum.RECEIVE.getCode());
            entity.setUpdateTime(new Date());
            return entity;
        }).collect(Collectors.toList());

        this.updateBatchById(collect);

        //更新采购单对应采购需求状态
        collect.forEach(entity->{
            List<PurchaseDetailEntity> detailEntities = purchaseDetailService.list(
                    new QueryWrapper<PurchaseDetailEntity>().eq("purchase_id", entity.getId()));

            List<PurchaseDetailEntity> purchaseDetailEntities = detailEntities.stream().map(detail -> {
                detail.setStatus(WareConstant.PurchaseDetailStatusEnum.BUYING.getCode());
                return detail;
            }).collect(Collectors.toList());
            purchaseDetailService.updateBatchById(purchaseDetailEntities);
        });
    }

    @Override
    public void finishPurchase(PurchaseDoneVo purchaseDoneVo) {

    }
}

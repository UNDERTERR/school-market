package com.xiaojie.ware.controller;

import com.xiaojie.common.utils.PageUtils;
import com.xiaojie.common.utils.R;
import com.xiaojie.ware.entity.PurchaseEntity;
import com.xiaojie.ware.service.PurchaseService;
import com.xiaojie.ware.vo.MergeVo;
import com.xiaojie.ware.vo.PurchaseDoneVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;



/**
 * 采购信息
 */
@RestController
@RequestMapping("ware/purchase")
public class PurchaseController {
    @Autowired
    private PurchaseService purchaseService;

    /**
     * 列表
     */
    @GetMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = purchaseService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @GetMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		PurchaseEntity purchase = purchaseService.getById(id);

        return R.ok().put("purchase", purchase);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    public R save(@RequestBody PurchaseEntity purchase){
		purchaseService.save(purchase);

        return R.ok();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    public R update(@RequestBody PurchaseEntity purchase){
		purchaseService.updateById(purchase);

        return R.ok();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		purchaseService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }


    @GetMapping("/unreceive/list")
    public R listUnreceive(@RequestParam Map<String, Object> params) {
        PageUtils page = purchaseService.listUnreceive(params);
        return R.ok().put("page", page);
    }


    /**
     * 合并采购需求
     * @param mergeVo
     * @return
     */
    @PostMapping("/merge")
    public R mergePurchaseDetail(@RequestBody MergeVo mergeVo) {
        purchaseService.mergePurchaseDetail(mergeVo);
        return R.ok();
    }


    /**
     * 领取采购单
     * @param ids
     * @return
     */
    @PostMapping("/received")
    public R ReceivedPurchase(@RequestBody List<Long> ids) {
        purchaseService.ReceivedPurchase(ids);
        return R.ok();
    }

    @PostMapping("/done")
    public R finishPurchase(@RequestBody PurchaseDoneVo purchaseDoneVo) {
        purchaseService.finishPurchase(purchaseDoneVo);
        return R.ok();
    }
}

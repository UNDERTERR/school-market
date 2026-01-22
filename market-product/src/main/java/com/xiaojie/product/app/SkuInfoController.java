package com.xiaojie.product.app;


import com.xiaojie.common.utils.PageUtils;
import com.xiaojie.common.utils.R;
import com.xiaojie.product.entity.SkuInfoEntity;
import com.xiaojie.product.service.SkuInfoService;
import com.xiaojie.product.vo.SkuItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;

@RestController
@RequestMapping("product/skuinfo")
public class SkuInfoController {
    @Autowired
    private SkuInfoService skuInfoService;


    /**
     * 获取商品详情信息
     */
    @GetMapping("/{skuId}")
    public R getSkuItem(@PathVariable("skuId") Long skuId) {
        SkuItemVo skuItemVo = skuInfoService.item(skuId);
        return R.ok().put("data", skuItemVo);
    }

    /**
     * 列表
     */
    @GetMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = skuInfoService.queryPageByCondition(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @GetMapping("/info/{skuId}")
    public R info(@PathVariable("skuId") Long skuId){
        SkuInfoEntity skuInfo = skuInfoService.getById(skuId);

        return R.ok().put("skuInfo", skuInfo);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    public R save(@RequestBody SkuInfoEntity skuInfo){
        skuInfoService.save(skuInfo);

        return R.ok();
    }



    /**
     * 修改
     */
    @PutMapping("/update")
    public R update(@RequestBody SkuInfoEntity skuInfo){
        skuInfoService.updateById(skuInfo);

        return R.ok();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete")
    public R delete(@RequestBody Long[] skuIds){
        skuInfoService.removeByIds(Arrays.asList(skuIds));

        return R.ok();
    }

}

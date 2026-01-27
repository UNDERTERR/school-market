package com.xiaojie.search.controller;

import com.xiaojie.common.exception.BizCodeEnum;
import com.xiaojie.common.to.es.SkuESModel;
import com.xiaojie.common.utils.R;
import com.xiaojie.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
public class ElasticSaveController {
    @Autowired
    private ProductSaveService productSaveService;

    @PostMapping("/product")
    public R saveProductAsIndices(@RequestBody List<SkuESModel> skuESModels) {
        boolean status = false;
        try {
            status = productSaveService.saveProductAsIndices(skuESModels);
        } catch (Exception e) {
            log.error("远程保存索引失败");
        }
        if (status) {
            return R.ok();
        } else {
            return R.error(BizCodeEnum.PRODUCT_UP_EXCEPTION.getCode(), BizCodeEnum.PRODUCT_UP_EXCEPTION.getMsg());
        }

    }
}

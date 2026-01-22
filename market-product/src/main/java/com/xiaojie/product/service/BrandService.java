package com.xiaojie.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaojie.common.utils.PageUtils;
import com.xiaojie.product.entity.AttrAttrgroupRelationEntity;
import com.xiaojie.product.entity.BrandEntity;

import java.util.Map;

public interface BrandService extends IService<BrandEntity> {
    PageUtils queryPage(Map<String, Object> params);

    void updateCascade(BrandEntity brand);
}

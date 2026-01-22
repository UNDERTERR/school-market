package com.xiaojie.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaojie.common.utils.PageUtils;
import com.xiaojie.product.entity.SkuImagesEntity;

import java.util.Map;

public interface SkuImagesService extends IService<SkuImagesEntity> {
    PageUtils queryPage(Map<String, Object> params);
}

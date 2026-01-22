package com.xiaojie.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaojie.common.utils.PageUtils;
import com.xiaojie.product.entity.SpuInfoDescEntity;

import java.util.Map;

public interface SpuInfoDescService extends IService<SpuInfoDescEntity> {

    PageUtils queryPage(Map<String, Object> params);
    void saveSpuInfoDesc(SpuInfoDescEntity descEntity);
}

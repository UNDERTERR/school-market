package com.xiaojie.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaojie.common.utils.PageUtils;
import com.xiaojie.product.entity.SpuCommentEntity;

import java.util.Map;

public interface SpuCommentService extends IService<SpuCommentEntity> {
    PageUtils queryPage(Map<String, Object> params);
}

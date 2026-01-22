package com.xiaojie.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaojie.common.utils.PageUtils;
import com.xiaojie.product.entity.CommentReplayEntity;

import java.util.Map;

public interface CommentReplayService extends IService<CommentReplayEntity> {
    PageUtils queryPage(Map<String, Object> params);
}

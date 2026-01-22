package com.xiaojie.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaojie.common.utils.PageUtils;
import com.xiaojie.product.entity.AttrGroupEntity;
import com.xiaojie.product.vo.AttrGroupWithAttrVo;

import java.util.List;
import java.util.Map;

public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageUtils queryPage(Map<String, Object> params, long catelogId);

    List<AttrGroupWithAttrVo> getAttrGroupWithAttrByCatelogId(Long catId);
}

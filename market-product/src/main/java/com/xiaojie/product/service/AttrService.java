package com.xiaojie.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaojie.common.utils.PageUtils;
import com.xiaojie.product.entity.AttrAttrgroupRelationEntity;
import com.xiaojie.product.entity.AttrEntity;
import com.xiaojie.product.entity.ProductAttrValueEntity;
import com.xiaojie.product.vo.AttrRespVo;
import com.xiaojie.product.vo.AttrVo;

import java.util.List;
import java.util.Map;

public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params, long catelogId, String attrType);


    void saveAttr(AttrVo attr);

    AttrRespVo getAttrInfo(Long attrId);

    void updateAttr(AttrVo attr);

    List<AttrEntity> getRelationAttr(Long attrgroupId);

    PageUtils getNoRelationAttr(Long attrgroupId, Map<String, Object> params);

    void saveRelationBatch(List<AttrAttrgroupRelationEntity> attrGroupEntities);

    List<ProductAttrValueEntity> listAttrsforSpu(Long spuId);

    void updateSpuAttrs(Long spuId, List<ProductAttrValueEntity> attrValueEntities);

    List<Long> selectSearchAttrIds(List<Long> attrIds);
}

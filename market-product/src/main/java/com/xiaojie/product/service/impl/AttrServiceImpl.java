package com.xiaojie.product.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaojie.common.utils.PageUtils;
import com.xiaojie.product.dao.AttrDao;

import com.xiaojie.product.entity.AttrAttrgroupRelationEntity;
import com.xiaojie.product.entity.AttrEntity;
import com.xiaojie.product.entity.ProductAttrValueEntity;
import com.xiaojie.product.service.AttrService;
import com.xiaojie.product.vo.AttrRespVo;
import com.xiaojie.product.vo.AttrVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    /**
     * TODO
     * @param params
     * @param catelogId
     * @param attrType
     * @return
     */

    @Override
    public PageUtils queryPage(Map<String, Object> params, long catelogId, String attrType) {
        return null;
    }

    @Override
    public void saveAttr(AttrVo attr) {

    }

    @Override
    public AttrRespVo getAttrInfo(Long attrId) {
        return null;
    }

    @Override
    public void updateAttr(AttrVo attr) {

    }

    @Override
    public List<AttrEntity> getRelationAttr(Long attrgroupId) {
        return Collections.emptyList();
    }

    @Override
    public PageUtils getNoRelationAttr(Long attrgroupId, Map<String, Object> params) {
        return null;
    }

    @Override
    public void saveRelationBatch(List<AttrAttrgroupRelationEntity> attrGroupEntities) {

    }

    @Override
    public List<ProductAttrValueEntity> listAttrsforSpu(Long spuId) {
        return Collections.emptyList();
    }

    @Override
    public void updateSpuAttrs(Long spuId, List<ProductAttrValueEntity> attrValueEntities) {

    }

    @Override
    public List<Long> selectSearchAttrIds(List<Long> attrIds) {
        return baseMapper.selectSearchAttrIds(attrIds);
    }
}

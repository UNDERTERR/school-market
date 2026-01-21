package com.xiaojie.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaojie.common.utils.PageUtils;
import com.xiaojie.product.dao.AttrAttrgroupRelationDao;
import com.xiaojie.product.dao.AttrDao;

import com.xiaojie.product.dao.AttrGroupDao;
import com.xiaojie.product.dao.CategoryDao;
import com.xiaojie.product.entity.AttrAttrgroupRelationEntity;
import com.xiaojie.product.entity.AttrEntity;
import com.xiaojie.product.entity.AttrGroupEntity;
import com.xiaojie.product.entity.ProductAttrValueEntity;
import com.xiaojie.product.service.AttrService;
import com.xiaojie.product.service.CategoryService;
import com.xiaojie.product.service.ProductAttrValueService;
import com.xiaojie.product.vo.AttrRespVo;
import com.xiaojie.product.vo.AttrVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {
    @Autowired
    private AttrAttrgroupRelationDao attrAttrgroupRelationDao;

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private AttrGroupDao attrGroupDao;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductAttrValueService productAttrValueService;

    @Override
    public PageUtils queryPage(Map<String, Object> params, long catelogId, String attrType) {
        return null;
    }

    @Override
    @Transactional
    public void saveAttr(AttrVo attr) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr, attrEntity);
        this.save(attrEntity);
        if (attr.getAttrGroupId() != null) {
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
            attrAttrgroupRelationEntity.setAttrGroupId(attr.getAttrGroupId());
            attrAttrgroupRelationEntity.setAttrId(attrEntity.getAttrId());
            attrAttrgroupRelationDao.insert(attrAttrgroupRelationEntity);
        }
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
        List<AttrAttrgroupRelationEntity> relationEntities = attrAttrgroupRelationDao.selectList(
                new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", attrgroupId)
        );
        List<AttrEntity> attrEntities = relationEntities.stream().map((entity) -> {
            AttrEntity attrEntity = baseMapper.selectById(entity.getAttrId());
            return attrEntity;
        }).collect(Collectors.toList());
        return attrEntities;
    }

    @Override
    public PageUtils getNoRelationAttr(Long attrgroupId, Map<String, Object> params) {
        return null;
    }

    @Override
    public void saveRelationBatch(List<AttrAttrgroupRelationEntity> attrGroupEntities) {
        attrGroupEntities.forEach((entity) -> {
            attrAttrgroupRelationDao.insert(entity);
        });
    }

    @Override
    public List<ProductAttrValueEntity> listAttrsforSpu(Long spuId) {
        List<ProductAttrValueEntity> list = productAttrValueService.list(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id", spuId));
        return list;
    }

    @Override
    public void updateSpuAttrs(Long spuId, List<ProductAttrValueEntity> attrValueEntities) {
        for (ProductAttrValueEntity productAttrValueEntity : attrValueEntities) {
            productAttrValueEntity.setSpuId(spuId);
            productAttrValueService.update(productAttrValueEntity,
                    new UpdateWrapper<ProductAttrValueEntity>()
                            .eq("spu_id", spuId)      // 哪个商品
                            .eq("attr_id", productAttrValueEntity.getAttrId()));  // 哪个属性
        }
    }


    @Override
    public List<Long> selectSearchAttrIds(List<Long> attrIds) {
        return baseMapper.selectSearchAttrIds(attrIds);
    }
}
//        {
//            spuId: 1001,
//                    attrs: [
//              {attrId: 1, attrName: 屏幕尺寸, attrValue: 6.1英寸},
//              {attrId: 2, attrName: 处理器, attrValue: A17 Pro},
//              {attrId: 3, attrName: 摄像头, attrValue: 4800万像素}
//            ]
//        }

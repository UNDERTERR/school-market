package com.xiaojie.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaojie.common.utils.PageUtils;
import com.xiaojie.common.utils.Query;
import com.xiaojie.product.dao.AttrAttrgroupRelationDao;
import com.xiaojie.product.dao.AttrDao;

import com.xiaojie.product.dao.AttrGroupDao;
import com.xiaojie.product.dao.CategoryDao;
import com.xiaojie.product.entity.*;
import com.xiaojie.product.service.AttrService;
import com.xiaojie.product.service.CategoryService;
import com.xiaojie.product.service.ProductAttrValueService;
import com.xiaojie.product.vo.AttrRespVo;
import com.xiaojie.product.vo.AttrVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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
        //根据attrType进行查询，1规格参数，2销售属性
        QueryWrapper<AttrEntity> attrEntityQueryWrapper = new QueryWrapper<AttrEntity>().eq("attr_type", "base".equalsIgnoreCase(attrType) ? 1 : 0);
        //如果参数带有分类id，则按分类查询
        if (catelogId != 0) {
            attrEntityQueryWrapper.eq("catelog_id", catelogId);
        }
        String key = (String) params.get("key");
        //搜索的模糊查询
        if (!StringUtils.isEmpty(key)) {
            attrEntityQueryWrapper.and((wrapper) -> wrapper.eq("attr_id", key).or().like("attr_name", key));
        }
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                attrEntityQueryWrapper
        );

        List<AttrEntity> records = page.getRecords();
        List<AttrRespVo> collect = records.stream().map((entity) -> {
            AttrRespVo respVo = new AttrRespVo();
            BeanUtils.copyProperties(entity, respVo);
            //查询分类并设置分类名
            CategoryEntity categoryEntity = categoryDao.selectOne(new QueryWrapper<CategoryEntity>().eq("cat_id", entity.getCatelogId()));
            respVo.setCatelogName(categoryEntity.getName());
            //如果是查询规格参数才查询设置分组名
            if ("base".equalsIgnoreCase(attrType)) {
                //查询参数、分组关系
                AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = attrAttrgroupRelationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", entity.getAttrId()));
                //如果分组id不为空。则查出分组名
                if (attrAttrgroupRelationEntity != null && attrAttrgroupRelationEntity.getAttrGroupId() != null) {
                    AttrGroupEntity attrGroupEntity = attrGroupDao.selectOne(new QueryWrapper<AttrGroupEntity>().eq("attr_group_id", attrAttrgroupRelationEntity.getAttrGroupId()));
                    //设置分组名
                    respVo.setGroupName(attrGroupEntity.getAttrGroupName());
                }
            }
            return respVo;
        }).collect(Collectors.toList());

        PageUtils pageUtils = new PageUtils(page);
        pageUtils.setList(collect);
        return pageUtils;
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

    @Cacheable("attr")
    @Transactional
    @Override
    public AttrRespVo getAttrInfo(Long attrId) {
        AttrEntity attrEntity = this.baseMapper.selectById(attrId);

        AttrRespVo respVo = new AttrRespVo();

        BeanUtils.copyProperties(attrEntity,respVo);

        //查询并设置分组名
        AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = attrAttrgroupRelationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrEntity.getAttrId()));
        //如果分组id不为空。则查出分组名
        if (attrAttrgroupRelationEntity != null && attrAttrgroupRelationEntity.getAttrGroupId() != null) {
            AttrGroupEntity attrGroupEntity = attrGroupDao.selectOne(new QueryWrapper<AttrGroupEntity>().eq("attr_group_id", attrAttrgroupRelationEntity.getAttrGroupId()));
            //设置分组名
            respVo.setGroupName(attrGroupEntity.getAttrGroupName());
            respVo.setAttrGroupId(attrGroupEntity.getAttrGroupId());
        }
        //查询到分类信息
        CategoryEntity categoryEntity = categoryDao.selectOne(new QueryWrapper<CategoryEntity>().eq("cat_id", attrEntity.getCatelogId()));
        //设置分类名
        respVo.setCatelogName(categoryEntity.getName());
        //查询并设置分类路径
        Long[] catelogPathById = categoryService.findCatelogPathById(categoryEntity.getCatId());
        respVo.setCatelogPath(catelogPathById);
        return respVo;
    }

    @Override
    public void updateAttr(AttrVo attr) {
        AttrEntity entity = new AttrEntity();
        BeanUtils.copyProperties(attr,entity);
        this.baseMapper.updateById(entity);
        //只有当属性分组不为空时，说明更新的是规则参数，则需要更新关联表
        if (attr.getAttrGroupId() != null) {
            //查询属性-分组名对应关系
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
            attrAttrgroupRelationEntity.setAttrId(attr.getAttrId());
            attrAttrgroupRelationEntity.setAttrGroupId(attr.getAttrGroupId());

            Integer c = attrAttrgroupRelationDao.selectCount(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrAttrgroupRelationEntity.getAttrId()));
            if (c>0){
                attrAttrgroupRelationDao.update(attrAttrgroupRelationEntity, new UpdateWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attr.getAttrId()));
            }else {
                attrAttrgroupRelationDao.insert(attrAttrgroupRelationEntity);
            }
        }
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
        AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrgroupId);
        Long catelogId = attrGroupEntity.getCatelogId();

        QueryWrapper<AttrEntity> wrapper=new QueryWrapper<AttrEntity>().eq("catelog_id", catelogId)
                //过滤掉销售属性
                .and((wrpper)->{
                    wrpper.eq("attr_type", 1);
                });
        //模糊搜索条件
        String key = (String) params.get("key");
        if(!StringUtils.isEmpty(key)){
            wrapper.and((w)->{
                w.eq("attr_id",key).or().like("attr_name",key);
            });
        }
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),wrapper);
        List<AttrEntity> records = page.getRecords();
        //过滤掉已经关联的属性
        List<AttrEntity> collect = records.stream().filter((record) -> {
            Integer count = attrAttrgroupRelationDao.selectCount(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", record.getAttrId()));
            if (count > 0) {
                return false;
            } else {
                return true;
            }
        }).collect(Collectors.toList());
        page.setRecords(collect);
        return new PageUtils(page);
    }
//    过滤前（所有属性）：
//    SELECT * FROM attr_entity WHERE catelog_id = 225;
//+---------+-----------+----------+
//        | attr_id | attr_name | attr_type|
//            +---------+-----------+----------+
//            |    1    |    颜色   |    1     |  ← 规格参数
//|    2    |    内存   |    1     |  ← 规格参数
//|    3    | 上市时间  |    0     |  ← 销售属性
//|    4    |    版本   |    0     |  ← 销售属性
//+---------+-----------+----------+
//    过滤后（只要规格参数）：
//    SELECT * FROM attr_entity WHERE catelog_id = 225 AND attr_type = 1;
//+---------+-----------+----------+
//        | attr_id | attr_name | attr_type|
//            +---------+-----------+----------+
//            |    1    |    颜色   |    1     |  ← 保留
//|    2    |    内存   |    1     |  ← 保留
//+---------+-----------+----------+
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

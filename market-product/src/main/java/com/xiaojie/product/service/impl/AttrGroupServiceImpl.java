package com.xiaojie.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaojie.common.utils.PageUtils;
import com.xiaojie.common.utils.Query;
import com.xiaojie.product.dao.AttrGroupDao;
import com.xiaojie.product.entity.AttrEntity;
import com.xiaojie.product.entity.AttrGroupEntity;
import com.xiaojie.product.service.AttrGroupService;
import com.xiaojie.product.service.AttrService;
import com.xiaojie.product.vo.AttrGroupWithAttrVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Autowired
    private AttrService attrService;


    @Override
    public PageUtils queryPage(Map<String, Object> params, long catelogId) {
        QueryWrapper<AttrGroupEntity> queryWrapper = new QueryWrapper<>();
        if(catelogId!=0){
            queryWrapper.eq("catelog_id",catelogId);
        }
        String key = (String) params.get("key");
        if(!StringUtils.isEmpty(key)){
            queryWrapper.and(entity->{
                entity.eq("attr_group_id",key).or().like("attr_group_name",key);
            });
        }

        IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params), queryWrapper);
        return new PageUtils(page);
    }

    @Override
    public List<AttrGroupWithAttrVo> getAttrGroupWithAttrByCatelogId(Long catId) {
        List<AttrGroupEntity> attrGroupEntities = baseMapper.selectList(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catId));

        List<AttrGroupWithAttrVo> collect = attrGroupEntities.stream().map(group -> {
                    AttrGroupWithAttrVo vo = new AttrGroupWithAttrVo();
                    BeanUtils.copyProperties(group, vo);
                    List<AttrEntity> relationAttr = attrService.getRelationAttr(group.getAttrGroupId());
                    vo.setAttrs(relationAttr);
                    return vo;
                }
        ).collect(Collectors.toList());

        return collect;
    }
}

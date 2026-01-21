package com.xiaojie.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaojie.common.utils.PageUtils;
import com.xiaojie.common.utils.Query;
import com.xiaojie.product.dao.ProductAttrValueDao;
import com.xiaojie.product.entity.ProductAttrValueEntity;
import com.xiaojie.product.service.ProductAttrValueService;
import com.xiaojie.product.vo.SpuItemAttrGroupVo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service("productAttrValueService")
public class ProductAttrValueServiceImpl extends ServiceImpl<ProductAttrValueDao, ProductAttrValueEntity> implements ProductAttrValueService {
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<ProductAttrValueEntity> page = this.page(new Query<ProductAttrValueEntity>().getPage(params),
                new QueryWrapper<ProductAttrValueEntity>());
        return new PageUtils(page);
    }

    @Override
    public List<SpuItemAttrGroupVo> getProductGroupAttrsBySpuId(Long spuId, Long catalogId) {
        return baseMapper.getProductGroupAttrsBySpuId(spuId, catalogId);
    }
}

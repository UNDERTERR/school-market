package com.xiaojie.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaojie.common.utils.PageUtils;
import com.xiaojie.common.utils.Query;
import com.xiaojie.product.dao.SpuInfoDescDao;
import com.xiaojie.product.entity.SpuInfoDescEntity;
import com.xiaojie.product.service.SpuInfoDescService;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("spuInfoDesc")
public class SpuInfoDescImpl extends ServiceImpl<SpuInfoDescDao,SpuInfoDescEntity> implements SpuInfoDescService {
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoDescEntity> page = this.page(
                new Query<SpuInfoDescEntity>().getPage(params),
                new QueryWrapper<SpuInfoDescEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSpuInfoDesc(SpuInfoDescEntity descEntity) {
        this.baseMapper.insert(descEntity);
    }
}

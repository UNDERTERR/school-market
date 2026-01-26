package com.xiaojie.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaojie.common.utils.PageUtils;
import com.xiaojie.common.utils.Query;
import com.xiaojie.coupon.dao.SkuLadderDao;
import com.xiaojie.coupon.entity.SkuLadderEntity;
import com.xiaojie.coupon.service.SkuLadderService;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("skuLadderService")
public class SkuLadderServiceImpl extends ServiceImpl<SkuLadderDao, SkuLadderEntity> implements SkuLadderService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuLadderEntity> page = this.page(
                new Query<SkuLadderEntity>().getPage(params),
                new QueryWrapper<SkuLadderEntity>()
        );

        return new PageUtils(page);
    }

}
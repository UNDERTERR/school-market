package com.xiaojie.product.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaojie.common.utils.PageUtils;
import com.xiaojie.product.dao.CategoryBrandRelationDao;
import com.xiaojie.product.dao.CategoryDao;
import com.xiaojie.product.entity.CategoryEntity;
import com.xiaojie.product.service.CategoryService;
import com.xiaojie.product.vo.Catalog2Vo;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {



    @Autowired
    private CategoryBrandRelationServiceImpl categoryBrandRelationServiceImpl;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        return null;
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        return Collections.emptyList();
    }

    @Override
    public void removeMenuByIds(List<Long> asList) {

    }

    @Override
    public Long[] findCatelogPathById(Long categorygId) {
        return new Long[0];
    }

    @Override
    public void updateCascade(CategoryEntity category) {

    }

    @Override
    public List<CategoryEntity> getLevel1Catagories() {
        return Collections.emptyList();
    }

    @Override
    public Map<String, List<Catalog2Vo>> getCategoryMap() {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, List<Catalog2Vo>> getCatelogJsonDbWithRedisLock() {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, List<Catalog2Vo>> getCatelogJsonDbWithRedisson() {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, List<Catalog2Vo>> getCatelogJsonDbWithSpringCache() {
        return Collections.emptyMap();
    }
}

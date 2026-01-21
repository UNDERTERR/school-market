package com.xiaojie.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaojie.common.utils.PageUtils;
import com.xiaojie.common.utils.Query;
import com.xiaojie.product.dao.CategoryDao;
import com.xiaojie.product.entity.CategoryEntity;
import com.xiaojie.product.service.CategoryService;
import com.xiaojie.product.vo.Catalog2Vo;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {
    @Autowired
    private CategoryBrandRelationServiceImpl categoryBrandRelationServiceImpl;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private RedissonClient redissonClient;
    private static final String CATALOG_CACHE_KEY = "catalog:json";
    private static final String LOCK_KEY = "catalog:lock";

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {

        List<CategoryEntity> entities = baseMapper.selectList(null);
        List<CategoryEntity> collect = entities.stream()
                .filter(item -> item.getParentCid() == 0)
                .map(menu -> {
                    menu.setChildren(getChildrens(menu, entities));
                    return menu;
                })
                .sorted((menu1, menu2) -> {
                    return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
                })
                .collect(Collectors.toList());
        return collect;
    }

    @Override
    public void removeMenuByIds(List<Long> asList) {
        baseMapper.deleteBatchIds(asList);
    }

    @Override
    public Long[] findCatelogPathById(Long categorygId) {
        List<Long> path = new LinkedList<>();
        findPath(categorygId, path);
        Collections.reverse(path);
        Long[] objects = path.toArray(new Long[path.size()]);
        return objects;
    }

    /**
     * 管理员更新分类关系，因此会清除缓存
     * @param category
     */
    @Transactional
    @Override
    @CacheEvict(value = {"category"},allEntries = true)
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);
        if(!StringUtils.isEmpty(category.getName())){
            categoryBrandRelationServiceImpl.updateCategory(category);
        }
    }

    @Override
    public List<CategoryEntity> getLevel1Catagories() {
        List<CategoryEntity> parent_cid = this.list(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
        return parent_cid;
    }

    @Override
    public Map<String, List<Catalog2Vo>> getCatelogJsonDbWithRedisson() {
        // 1. 先查缓存
        String catalogJson = stringRedisTemplate.opsForValue().get(CATALOG_CACHE_KEY);
        if (StringUtils.isNotBlank(catalogJson)) {
            System.out.println("缓存命中");
            return JSON.parseObject(catalogJson, new TypeReference<Map<String, List<Catalog2Vo>>>() {});
        }
        // 2. 缓存未命中，获取分布式锁
        RLock lock = redissonClient.getLock(LOCK_KEY);
        try {
            // 等待锁，最多等5秒，锁30秒后自动过期（看门狗会自动续期）
            if (lock.tryLock(5, 30, TimeUnit.SECONDS)) {
                // 3. 双重检查，防止其他线程已经写入缓存
                catalogJson = stringRedisTemplate.opsForValue().get(CATALOG_CACHE_KEY);
                if (StringUtils.isNotBlank(catalogJson)) {
                    return JSON.parseObject(catalogJson, new TypeReference<Map<String, List<Catalog2Vo>>>() {});
                }
                // 4. 查数据库并写缓存
                System.out.println("缓存不命中，查询数据库");
                Map<String, List<Catalog2Vo>> categoriesDb = getCategoriesFromDb();
                String toJsonString = JSON.toJSONString(categoriesDb);
                stringRedisTemplate.opsForValue().set(CATALOG_CACHE_KEY, toJsonString, 1L, TimeUnit.HOURS);
                return categoriesDb;
            } else {
                // 获取锁失败，等待后重试
                Thread.sleep(100);
                return getCatelogJsonDbWithRedisson();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("获取锁被中断", e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }


    //功能函数

    private void findPath(Long categorygId, List<Long> path) {
        if (categorygId != 0) {
            path.add(categorygId);
            CategoryEntity byId = getById(categorygId);
            findPath(byId.getParentCid(), path);
        }
    }

    private Map<String, List<Catalog2Vo>> getCategoriesFromDb() {
        System.out.println("查询了数据库");
        //优化业务逻辑，仅查询一次数据库
        List<CategoryEntity> categoryEntities = this.list();
        //查出所有一级分类
        List<CategoryEntity> level1Categories = getCategoryByParentCid(categoryEntities, 0L);

        Map<String, List<Catalog2Vo>> listMap = level1Categories.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            //遍历查找出二级分类
            List<CategoryEntity> level2Categories = getCategoryByParentCid(categoryEntities, v.getCatId());
            List<Catalog2Vo> catalog2Vos = null;

            if (level2Categories != null) {
                //封装二级分类到vo并且查出其中的三级分类
                catalog2Vos = level2Categories.stream().map(cat -> {
                    //遍历查出三级分类并封装
                    List<CategoryEntity> level3Catagories = getCategoryByParentCid(categoryEntities, cat.getCatId());
                    List<Catalog2Vo.Catalog3Vo> catalog3Vos = null;

                    if (level3Catagories != null) {
                        catalog3Vos = level3Catagories.stream()
                                .map(level3 -> new Catalog2Vo.Catalog3Vo(level3.getParentCid().toString(), level3.getCatId().toString(), level3.getName()))
                                .collect(Collectors.toList());
                    }
                    Catalog2Vo catalog2Vo = new Catalog2Vo(v.getCatId().toString(), cat.getCatId().toString(), cat.getName(), catalog3Vos);
                    return catalog2Vo;
                }).collect(Collectors.toList());
            }
            return catalog2Vos;
        }));
        return listMap;
    }

    private List<CategoryEntity> getCategoryByParentCid(List<CategoryEntity> categoryEntities, long l) {
        List<CategoryEntity> collect = categoryEntities.stream().filter(cat -> cat.getParentCid() == l).collect(Collectors.toList());
        return collect;
    }

    private List<CategoryEntity> getChildrens(CategoryEntity categoryEntity, List<CategoryEntity> entities) {
        List<CategoryEntity> collect = entities.stream()
                .filter(item -> item.getParentCid() == categoryEntity.getCatId())
                .map(menu -> {
                    menu.setChildren(getChildrens(menu, entities));
                    return menu;
                })
                .sorted((menu1, menu2) -> {
                    return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
                })
                .collect(Collectors.toList());

        return collect;
    }
}

package com.xiaojie.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaojie.common.constant.ProductConstant;
import com.xiaojie.common.to.SkuHasStockVo;
import com.xiaojie.common.to.SkuReductionTo;
import com.xiaojie.common.to.es.SkuESModel;
import com.xiaojie.common.to.SpuBoundTo;
import com.xiaojie.common.utils.PageUtils;
import com.xiaojie.common.utils.Query;
import com.xiaojie.common.utils.R;
import com.xiaojie.product.dao.SpuInfoDao;
import com.xiaojie.product.entity.*;
import com.xiaojie.product.feign.CouponFeignService;
import com.xiaojie.product.feign.SearchFeignService;
import com.xiaojie.product.feign.WareFeignService;
import com.xiaojie.product.service.*;
import com.xiaojie.product.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Slf4j
@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    private SpuInfoDescService spuInfoDescService;

    @Autowired
    private SpuImagesService spuImagesService;

    @Autowired
    private AttrService attrService;

    @Autowired
    private ProductAttrValueService productAttrValueService;

    @Autowired
    private SkuInfoService skuInfoService;

    @Autowired
    private SkuImagesService skuImagesService;

    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    private CouponFeignService couponFeignService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private WareFeignService wareFeignService;

    @Autowired
    private SearchFeignService searchFeignService;

    @Autowired
    private ThreadPoolExecutor executor;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void saveSpuSaveVo(SpuSaveVo spuSaveVo) {

        long startTime = System.currentTimeMillis();

        try {
            // 1. 保存SPU基本信息（核心数据）
            SpuInfoEntity spuInfoEntity = saveSpuInfo(spuSaveVo);

            // 2. 并行保存关联数据
            List<CompletableFuture<Void>> futures = new ArrayList<>();

            // 保存SPU描述图片
            if (spuSaveVo.getDecript() != null && !spuSaveVo.getDecript().isEmpty()) {
                futures.add(CompletableFuture.runAsync(() ->
                        saveSpuDesc(spuInfoEntity.getId(), spuSaveVo.getDecript()), executor));
            }

            // 保存SPU图片集
            if (spuSaveVo.getImages() != null && !spuSaveVo.getImages().isEmpty()) {
                futures.add(CompletableFuture.runAsync(() ->
                        saveSpuImages(spuInfoEntity.getId(), spuSaveVo.getImages()), executor));
            }

            // 保存SPU规格参数
            if (spuSaveVo.getBaseAttrs() != null && !spuSaveVo.getBaseAttrs().isEmpty()) {
                futures.add(CompletableFuture.runAsync(() ->
                        saveSpuAttrs(spuInfoEntity, spuSaveVo.getBaseAttrs()), executor));
            }

            // 保存SPU积分信息
            if (spuSaveVo.getBounds() != null) {
                futures.add(CompletableFuture.runAsync(() ->
                        saveSpuBounds(spuInfoEntity.getId(), spuSaveVo.getBounds()), executor));
            }

            // 保存SKU信息（依赖SPU ID，在基本信息保存后执行）
            if (spuSaveVo.getSkus() != null && !spuSaveVo.getSkus().isEmpty()) {
                futures.add(CompletableFuture.runAsync(() ->
                        saveSkus(spuInfoEntity, spuSaveVo.getSkus()), executor));
            }

            // 等待所有任务完成
            if (!futures.isEmpty()) {
                CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get(10, TimeUnit.SECONDS);
            }

            long endTime = System.currentTimeMillis();
            log.info("保存SPU信息完成，spuId: {}, 耗时: {}ms", spuInfoEntity.getId(), endTime - startTime);

        } catch (Exception e) {
            log.error("保存SPU信息失败", e);
            throw new RuntimeException("保存商品信息失败: " + e.getMessage(), e);
        }


    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();

        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            wrapper.and((w) -> {
                w.eq("id", key).or().like("spu_name", key);
            });
        }
        String status = (String) params.get("status");
        if (!StringUtils.isEmpty(status)) {
            wrapper.eq("publish_status", status);
        }

        String brandId = (String) params.get("brandId");
        if (!StringUtils.isEmpty(brandId) && !"0".equalsIgnoreCase(brandId)) {
            wrapper.eq("brand_id", brandId);
        }

        String catelogId = (String) params.get("catelogId");
        if (!StringUtils.isEmpty(catelogId) && !"0".equalsIgnoreCase(catelogId)) {
            wrapper.eq("catalog_id", catelogId);
        }

        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void upSpuForSearch(Long spuId) {
        //1、查出当前spuId对应的所有sku信息,品牌的名字
        List<SkuInfoEntity> skuInfoEntities = skuInfoService.getSkusBySpuId(spuId);

        //TODO 4、查出当前sku的所有可以被用来检索的规格属性
        List<ProductAttrValueEntity> productAttrValueEntities = productAttrValueService
                .list(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id", spuId));

        List<Long> attrIds = productAttrValueEntities.stream().map(attr -> {
            return attr.getAttrId();
        }).collect(Collectors.toList());

        List<Long> searchIds = attrService.selectSearchAttrIds(attrIds);
        Set<Long> ids = new HashSet<>(searchIds);

        List<SkuESModel.Attr> searchAttrs = productAttrValueEntities.stream().filter(entity -> {
            return ids.contains(entity.getAttrId());
        }).map(entity -> {
            SkuESModel.Attr attr = new SkuESModel.Attr();
            BeanUtils.copyProperties(entity, attr);
            return attr;
        }).collect(Collectors.toList());

        //TODO 1、发送远程调用，库存系统查询是否有库存
        Map<Long, Boolean> stockMap = null;
        try {
            List<Long> longList = skuInfoEntities.stream().map(SkuInfoEntity::getSkuId).collect(Collectors.toList());
            List<SkuHasStockVo> skuHasStocks = wareFeignService.getSkuHasStocks(longList);

            stockMap = skuHasStocks.stream().collect(Collectors.toMap(SkuHasStockVo::getSkuId, SkuHasStockVo::getHasStock));

        } catch (Exception e) {
            log.error("远程调用库存服务失败,原因{}", e);
        }

        //2、封装每个sku的信息
        Map<Long, Boolean> finalStockMap = stockMap;

        List<SkuESModel> SkuESModels = skuInfoEntities.stream().map(sku -> {
            SkuESModel SkuESModel = new SkuESModel();
            BeanUtils.copyProperties(sku, SkuESModel);
            SkuESModel.setSkuPrice(sku.getPrice());
            SkuESModel.setSkuImg(sku.getSkuDefaultImg());
            //TODO 2、热度评分。
            SkuESModel.setHotScore(0L);
            //TODO 3、查询品牌和分类的名字信息
            BrandEntity brandEntity = brandService.getById(sku.getBrandId());

            SkuESModel.setBrandName(brandEntity.getName());
            SkuESModel.setBrandImg(brandEntity.getLogo());
            CategoryEntity categoryEntity = categoryService.getById(sku.getCatalogId());
            SkuESModel.setCatalogName(categoryEntity.getName());
            //设置可搜索属性
            SkuESModel.setAttrs(searchAttrs);
            //设置是否有库存
            SkuESModel.setHasStock(finalStockMap == null ? false : finalStockMap.get(sku.getSkuId()));
            return SkuESModel;
        }).collect(Collectors.toList());

        //TODO 5、将数据发给es进行保存：market-search
        R r = searchFeignService.saveProductAsIndices(SkuESModels);
        if (r.getCode() == 0) {
            this.baseMapper.upSpuStatus(spuId, ProductConstant.ProductStatusEnum.SPU_UP.getCode());
        } else {
            log.error("商品远程es保存失败");
        }
    }

    @Override
    public SpuInfoEntity getSpuBySkuId(Long skuId) {
        SkuInfoEntity skuInfoEntity = skuInfoService.getById(skuId);
        SpuInfoEntity spu = this.getById(skuInfoEntity.getSpuId());

        BrandEntity brandEntity = brandService.getById(spu.getBrandId());
        spu.setBrandName(brandEntity.getName());
        return spu;
    }


    /**
     * 保存SPU基本信息
     */
    private SpuInfoEntity saveSpuInfo(SpuSaveVo spuSaveVo) {
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(spuSaveVo, spuInfoEntity);
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUpdateTime(new Date());
        this.baseMapper.insert(spuInfoEntity);
        return spuInfoEntity;
    }

    /**
     * 保存SPU描述图片
     */
    private void saveSpuDesc(Long spuId, List<String> decript) {
        SpuInfoDescEntity descEntity = new SpuInfoDescEntity();
        descEntity.setSpuId(spuId);
        descEntity.setDecript(String.join(",", decript));
        spuInfoDescService.saveSpuInfoDesc(descEntity);
    }

    /**
     * 保存SPU图片集
     */
    private void saveSpuImages(Long spuId, List<String> images) {
        spuImagesService.saveImages(spuId, images);
    }

    /**
     * 保存SPU规格参数
     */
    private void saveSpuAttrs(SpuInfoEntity spuInfoEntity, List<BaseAttrs> baseAttrs) {

        List<ProductAttrValueEntity> collect = baseAttrs.stream().map(attr -> {
            ProductAttrValueEntity entity = new ProductAttrValueEntity();
            entity.setSpuId(spuInfoEntity.getId());
            entity.setAttrId(attr.getAttrId());
            AttrEntity byId = attrService.getById(attr.getAttrId());
            entity.setAttrName(byId.getAttrName());
            entity.setAttrValue(attr.getAttrValues());
            entity.setQuickShow(attr.getShowDesc());
            return entity;
        }).collect(Collectors.toList());
        productAttrValueService.saveBatch(collect);

    }

    /**
     * 保存SPU积分信息
     */
    private void saveSpuBounds(Long spuId, Bounds bounds) {
        SpuBoundTo spuBoundTo = new SpuBoundTo();
        BeanUtils.copyProperties(bounds, spuBoundTo);
        spuBoundTo.setSpuId(spuId);
        couponFeignService.saveSpuBounds(spuBoundTo);
    }

    /**
     * 保存SKU信息（批量优化版本）
     */
    private void saveSkus(SpuInfoEntity spuInfoEntity, List<Skus> skus) {
        if (skus == null || skus.isEmpty()) {
            return;
        }

        // 1. 批量保存SKU基本信息
        List<SkuInfoEntity> skuInfoEntities = skus.stream().map(sku -> {
            SkuInfoEntity entity = new SkuInfoEntity();
            BeanUtils.copyProperties(sku, entity);
            entity.setSpuId(spuInfoEntity.getId());
            entity.setCatalogId(spuInfoEntity.getCatalogId());
            entity.setBrandId(spuInfoEntity.getBrandId());
            entity.setSaleCount(0L);

            // 设置默认图片
            sku.getImages().stream()
                    .filter(img -> img.getDefaultImg() == 1)
                    .findFirst()
                    .ifPresent(img -> entity.setSkuDefaultImg(img.getImgUrl()));

            return entity;
        }).collect(Collectors.toList());

        skuInfoService.saveBatch(skuInfoEntities);

        // 2. 批量收集所有SKU的关联数据
        List<SkuImagesEntity> allSkuImages = new ArrayList<>();
        List<SkuSaleAttrValueEntity> allSaleAttrs = new ArrayList<>();
        List<SkuReductionTo> allReductions = new ArrayList<>();

        for (int i = 0; i < skus.size(); i++) {
            Skus sku = skus.get(i);
            Long skuId = skuInfoEntities.get(i).getSkuId();
            // 收集SKU图片
            if (sku.getImages() != null) {
                List<SkuImagesEntity> skuImages = sku.getImages().stream()
                        .map(img -> {
                            SkuImagesEntity entity = new SkuImagesEntity();
                            BeanUtils.copyProperties(img, entity);
                            entity.setSkuId(skuId);
                            return entity;
                        })
                        .filter(img -> !StringUtils.isEmpty(img.getImgUrl()))
                        .collect(Collectors.toList());
                allSkuImages.addAll(skuImages);
            }

            // 收集SKU销售属性
            if (sku.getAttr() != null) {
                List<SkuSaleAttrValueEntity> saleAttrs = sku.getAttr().stream()
                        .map(attr -> {
                            SkuSaleAttrValueEntity entity = new SkuSaleAttrValueEntity();
                            BeanUtils.copyProperties(attr, entity);
                            entity.setSkuId(skuId);
                            return entity;
                        })
                        .collect(Collectors.toList());
                allSaleAttrs.addAll(saleAttrs);
            }
            // 收集SKU优惠信息
            SkuReductionTo reduction = new SkuReductionTo();
            BeanUtils.copyProperties(sku, reduction);
            reduction.setSkuId(skuId);
            allReductions.add(reduction);
        }
        // 3. 批量保存关联数据
        if (!allSkuImages.isEmpty()) {
            skuImagesService.saveBatch(allSkuImages);
        }

        if (!allSaleAttrs.isEmpty()) {
            skuSaleAttrValueService.saveBatch(allSaleAttrs);
        }
        // 批量保存优惠信息
        if (!allReductions.isEmpty()) {
            allReductions.forEach(skuReductionTo -> {
                if (skuReductionTo.getFullCount() > 0 || skuReductionTo.getFullPrice().compareTo(new BigDecimal("0")) == 1) {
                    R r = couponFeignService.saveSkuReductionTo(skuReductionTo);
                }
            });
        }
    }
}
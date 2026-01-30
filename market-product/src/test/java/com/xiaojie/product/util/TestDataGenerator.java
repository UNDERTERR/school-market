package com.xiaojie.product.util;

import com.xiaojie.product.entity.CategoryEntity;
import com.xiaojie.product.entity.BrandEntity;
import com.xiaojie.product.entity.SpuInfoEntity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Random;

/**
 * 测试数据生成工具
 */
public class TestDataGenerator {

    private static final Random random = new Random();

    /**
     * 生成分类测试数据
     */
    public static CategoryEntity createCategoryEntity() {
        CategoryEntity category = new CategoryEntity();
        category.setCatId(System.currentTimeMillis());
        category.setName("测试分类_" + System.currentTimeMillis());
        category.setParentCid(0L);
        category.setCatLevel(1);
        category.setShowStatus(1);
        category.setSort(random.nextInt(100));
        category.setIcon("http://example.com/icon.png");
        category.setProductUnit("个");
        category.setProductCount(0);
        // setDeleted方法不存在，CategoryEntity使用@TableLogic在showStatus字段
        return category;
    }

    /**
     * 生成品牌测试数据
     */
    public static BrandEntity createBrandEntity() {
        BrandEntity brand = new BrandEntity();
        brand.setBrandId(System.currentTimeMillis());
        brand.setName("测试品牌_" + System.currentTimeMillis());
        brand.setLogo("http://example.com/logo.png");
        brand.setDescript("测试品牌描述");
        brand.setShowStatus(1);
        brand.setFirstLetter("T");
        brand.setSort(random.nextInt(100));
        // BrandEntity没有deleted字段，使用@TableLogic在showStatus字段
        return brand;
    }

    /**
     * 生成SPU测试数据
     */
    public static SpuInfoEntity createSpuInfoEntity() {
        SpuInfoEntity spu = new SpuInfoEntity();
        spu.setId(System.currentTimeMillis());
        spu.setBrandId(1L);
        spu.setCatalogId(1L);
        spu.setSpuName("测试SPU_" + System.currentTimeMillis());
        spu.setSpuDescription("测试SPU描述");
        spu.setPublishStatus(1);
        spu.setCreateTime(new Date());
        spu.setUpdateTime(new Date());
        // SpuInfoEntity没有deleted字段
        return spu;
    }

    /**
     * 生成随机字符串
     */
    public static String randomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    /**
     * 生成随机数字
     */
    public static int randomNumber(int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }

    /**
     * 生成随机价格
     */
    public static BigDecimal randomPrice(double min, double max) {
        double price = min + (max - min) * random.nextDouble();
        return BigDecimal.valueOf(Math.round(price * 100.0) / 100.0);
    }
}
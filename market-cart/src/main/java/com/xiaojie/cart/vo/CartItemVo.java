package com.xiaojie.cart.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.List;

@Schema(description = "购物车商品项")
public class CartItemVo {

@Schema(description = "商品SKU ID", example = "1")
    private Long skuId;

    @Schema(description = "是否选中", example = "true")
    private Boolean check = true;

    @Schema(description = "商品标题", example = "iPhone 15 Pro")
    private String title;

    @Schema(description = "商品图片URL", example = "https://example.com/image.jpg")
    private String image;

    /**
     * 商品套餐属性
     */
    @Schema(description = "商品规格属性列表")
    private List<String> skuAttrValues;

    @Schema(description = "商品单价", example = "99.99")
    private BigDecimal price;

    @Schema(description = "购买数量", example = "2")
    private Integer count;

    @Schema(description = "商品总价", example = "199.98")
    private BigDecimal totalPrice;

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public Boolean getCheck() {
        return check;
    }

    public void setCheck(Boolean check) {
        this.check = check;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<String> getSkuAttrValues() {
        return skuAttrValues;
    }

    public void setSkuAttrValues(List<String> skuAttrValues) {
        this.skuAttrValues = skuAttrValues;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    /**
     * 当前购物车项总价等于单价x数量
     *
     * @return
     */
    public BigDecimal getTotalPrice() {
        return price.multiply(new BigDecimal(count));
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
}

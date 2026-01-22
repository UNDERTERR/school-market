package com.xiaojie.product.feign;

import com.xiaojie.common.to.SkuReductionTo;
import com.xiaojie.common.to.SpuBoundTo;
import com.xiaojie.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient("market-coupon")
public interface CouponFeignService {

    @PostMapping("/coupon/spubounds/save")
    R saveSpuBounds(@RequestBody SpuBoundTo spuBoundTo);

    @PostMapping("/coupon/skufullreduction/saveInfo")
    R saveSkuReductionTo(@RequestBody SkuReductionTo skuReductionTo);
}

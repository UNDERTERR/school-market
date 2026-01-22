package com.xiaojie.product.feign;

import com.xiaojie.common.to.es.SkuESModel;
import com.xiaojie.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;



@FeignClient("market-search")
public interface SearchFeignService {
    @PostMapping("/product")
    R saveProductAsIndices(@RequestBody List<SkuESModel> SkuESModels);
}

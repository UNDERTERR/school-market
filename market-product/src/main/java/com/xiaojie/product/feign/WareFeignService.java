package com.xiaojie.product.feign;

import com.xiaojie.common.to.SkuHasStockVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


@FeignClient("market-ware")
public interface WareFeignService {
    @GetMapping("/ware/waresku/getSkuHasStocks")
    List<SkuHasStockVo> getSkuHasStocks(@RequestBody List<Long> ids);
}

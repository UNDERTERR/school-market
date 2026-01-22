package com.xiaojie.product.feign.fallback;

import com.xiaojie.common.exception.BizCodeEnum;
import com.xiaojie.common.utils.R;
import com.xiaojie.product.feign.SeckillFeignService;
import org.springframework.stereotype.Component;


@Component
public class SeckillFallbackService implements SeckillFeignService {
    @Override
    public R getSeckillSkuInfo(Long skuId) {
        return R.error(BizCodeEnum.READ_TIME_OUT_EXCEPTION.getCode(), BizCodeEnum.READ_TIME_OUT_EXCEPTION.getMsg());
    }
}

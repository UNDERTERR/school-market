package com.xiaojie.seckill.service.impl;

import com.xiaojie.seckill.service.SecKillService;
import com.xiaojie.seckill.to.SeckillSkuRedisTo;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service("secKillService")
public class SecKillServiceImpl implements SecKillService {
    @Override
    public void uploadSeckillSkuLatest3Days() {

    }

    @Override
    public List<SeckillSkuRedisTo> getCurrentSeckillSkus() {
        return Collections.emptyList();
    }

    @Override
    public SeckillSkuRedisTo getSeckillSkuInfo(Long skuId) {
        return null;
    }

    @Override
    public String kill(String killId, String key, Integer num) throws InterruptedException {
        return "";
    }
}

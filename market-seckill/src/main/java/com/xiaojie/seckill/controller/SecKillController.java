package com.xiaojie.seckill.controller;

import com.xiaojie.common.utils.R;
import com.xiaojie.seckill.service.SecKillService;
import com.xiaojie.seckill.to.SeckillSkuRedisTo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@Tag(name = "秒杀管理", description = "商品秒杀相关接口")
public class SecKillController {

    @Autowired
    private SecKillService secKillService;

/**
     * 当前时间可以参与秒杀的商品信息
     * @return
     */
    @GetMapping(value = "/getCurrentSeckillSkus")
    @ResponseBody
    @Operation(summary = "获取当前秒杀商品", description = "获取当前时间可以参与秒杀的商品信息")
    public R getCurrentSeckillSkus() {
        //获取到当前可以参加秒杀商品的信息
        List<SeckillSkuRedisTo> vos = secKillService.getCurrentSeckillSkus();

        return R.ok().setData(vos);
    }

@ResponseBody
    @GetMapping(value = "/getSeckillSkuInfo/{skuId}")
    @Operation(summary = "获取秒杀商品详情", description = "根据商品SKU ID获取秒杀商品的详细信息")
    public R getSeckillSkuInfo(@Parameter(description = "商品SKU ID") @PathVariable("skuId") Long skuId) {
        SeckillSkuRedisTo to = secKillService.getSeckillSkuInfo(skuId);
        return R.ok().setData(to);
    }


@GetMapping("/kill")
    @Operation(summary = "执行秒杀", description = "用户执行秒杀操作，返回订单号")
    public String kill(@Parameter(description = "秒杀商品ID") @RequestParam("killId") String killId,
                       @Parameter(description = "秒杀令牌") @RequestParam("key")String key,
                       @Parameter(description = "购买数量") @RequestParam("num")Integer num,
                       Model model) {
        String orderSn= null;
        try {
            orderSn = secKillService.kill(killId, key, num);
            model.addAttribute("orderSn", orderSn);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "success";
    }


}

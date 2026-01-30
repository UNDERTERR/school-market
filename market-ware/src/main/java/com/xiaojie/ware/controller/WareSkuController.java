package com.xiaojie.ware.controller;

import com.xiaojie.common.exception.BizCodeEnum;
import com.xiaojie.common.exception.NoStockException;
import com.xiaojie.common.to.SkuHasStockVo;
import com.xiaojie.common.utils.PageUtils;
import com.xiaojie.common.utils.R;
import com.xiaojie.ware.entity.WareSkuEntity;
import com.xiaojie.ware.service.WareSkuService;
import com.xiaojie.ware.vo.WareSkuLockVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * 商品库存
 *
 * @author Ethan
 * @email hongshengmo@163.com
 * @date 2020-05-27 23:15:25
 */
@RestController
@RequestMapping("ware/waresku")
@Tag(name = "商品库存管理", description = "商品库存相关接口")
public class WareSkuController {
    @Autowired
    private WareSkuService wareSkuService;


/**
     * 下订单时锁库存
     * @return
     */
    @PutMapping("/lock/order")
    @Operation(summary = "锁定库存", description = "为订单锁定指定商品的库存，防止超卖")
    public R orderLockStock(@Parameter(description = "库存锁定信息，包含商品SKU和数量") @RequestBody WareSkuLockVo lockVo) {
        try {
            Boolean lock = wareSkuService.orderLockStock(lockVo);
            return R.ok();
        } catch (NoStockException e) {
            return R.error(BizCodeEnum.NO_STOCK_EXCEPTION.getCode(), BizCodeEnum.NO_STOCK_EXCEPTION.getMsg());
        }
    }

/**
     * 列表
     */
    @GetMapping("/list")
    @Operation(summary = "分页查询库存", description = "根据条件分页查询商品库存列表")
    public R list(@Parameter(description = "查询条件，包含分页参数") @RequestParam Map<String, Object> params){
        PageUtils page = wareSkuService.queryPage(params);

        return R.ok().put("page", page);
    }


@GetMapping("/getSkuHasStocks")
    @Operation(summary = "查询商品库存状态", description = "批量查询商品是否有库存")
    public List<SkuHasStockVo> getSkuHasStocks(@Parameter(description = "商品SKU ID列表") @RequestBody List<Long> ids) {
        return wareSkuService.getSkuHasStocks(ids);
    }

/**
     * 信息
     */
    @GetMapping("/info/{id}")
    @Operation(summary = "获取库存详情", description = "根据库存ID获取商品库存详细信息")
    public R info(@Parameter(description = "库存ID") @PathVariable("id") Long id){
		WareSkuEntity wareSku = wareSkuService.getById(id);

        return R.ok().put("wareSku", wareSku);
    }

/**
     * 保存
     */
    @PostMapping("/save")
    @Operation(summary = "保存库存", description = "新增商品库存信息")
    public R save(@Parameter(description = "库存信息") @RequestBody WareSkuEntity wareSku){
		wareSkuService.save(wareSku);

        return R.ok();
    }

/**
     * 修改
     */
    @PutMapping("/update")
    @Operation(summary = "更新库存", description = "更新商品库存信息")
    public R update(@Parameter(description = "库存信息") @RequestBody WareSkuEntity wareSku){
		wareSkuService.updateById(wareSku);

        return R.ok();
    }

/**
     * 删除
     */
    @DeleteMapping("/delete")
    @Operation(summary = "删除库存", description = "根据库存ID删除商品库存信息")
    public R delete(@Parameter(description = "库存ID数组") @RequestBody Long[] ids){
		wareSkuService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}

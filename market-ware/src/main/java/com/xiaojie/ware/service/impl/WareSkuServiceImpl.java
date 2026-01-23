package com.xiaojie.ware.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaojie.common.exception.NoStockException;
import com.xiaojie.common.to.SkuHasStockVo;
import com.xiaojie.common.to.mq.OrderTo;
import com.xiaojie.common.to.mq.StockDetailTo;
import com.xiaojie.common.to.mq.StockLockedTo;
import com.xiaojie.common.utils.PageUtils;
import com.xiaojie.common.utils.Query;
import com.xiaojie.common.utils.R;
import com.xiaojie.ware.dao.WareSkuDao;
import com.xiaojie.ware.entity.WareOrderTaskDetailEntity;
import com.xiaojie.ware.entity.WareOrderTaskEntity;
import com.xiaojie.ware.entity.WareSkuEntity;
import com.xiaojie.ware.enume.OrderStatusEnum;
import com.xiaojie.ware.enume.WareTaskStatusEnum;
import com.xiaojie.ware.feign.OrderFeignService;
import com.xiaojie.ware.feign.ProductFeignService;
import com.xiaojie.ware.service.WareOrderTaskDetailService;
import com.xiaojie.ware.service.WareOrderTaskService;
import com.xiaojie.ware.service.WareSkuService;
import com.xiaojie.ware.vo.OrderItemVo;
import com.xiaojie.ware.vo.WareSkuLockVo;
import lombok.Data;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Autowired
    WareOrderTaskDetailService wareOrderTaskDetailService;

    @Autowired
    WareOrderTaskService wareOrderTaskService;

    @Autowired
    ProductFeignService productFeignService;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    OrderFeignService orderFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                new QueryWrapper<WareSkuEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {
        Integer count = this.baseMapper.selectCount(
                new QueryWrapper<WareSkuEntity>().eq("sku_id", skuId).eq("ware_id", wareId));
        if (count == 0) {
            WareSkuEntity wareSkuEntity = new WareSkuEntity();
            wareSkuEntity.setSkuId(skuId);
            wareSkuEntity.setWareId(wareId);
            wareSkuEntity.setStock(skuNum);
            wareSkuEntity.setStockLocked(0);
            //查出skuname并设置
            try {
                R info = productFeignService.info(skuId);
                Map<String, Object> data = (Map<String, Object>) info.get("skuInfo");

                wareSkuEntity.setSkuName((String) data.get("skuName"));
            } catch (Exception e) {
            }
            this.baseMapper.insert(wareSkuEntity);
        } else {
            this.baseMapper.addstock(skuId, wareId, skuNum);
        }
    }

    @Override
    public List<SkuHasStockVo> getSkuHasStocks(List<Long> ids) {
        List<SkuHasStockVo> skuHasStockVos = ids.stream().map(id -> {
            SkuHasStockVo skuHasStockVo = new SkuHasStockVo();
            skuHasStockVo.setSkuId(id);

            Integer count = baseMapper.getTotalStock(id);
            skuHasStockVo.setHasStock(count == null ? false : count > 0);
            return skuHasStockVo;
        }).collect(Collectors.toList());

        return skuHasStockVos;
    }

    @Transactional
    @Override
    public Boolean orderLockStock(WareSkuLockVo lockVo) {

        WareOrderTaskEntity taskEntity = new WareOrderTaskEntity();
        taskEntity.setOrderSn(lockVo.getOrderSn());
        taskEntity.setCreateTime(new Date());
        wareOrderTaskService.save(taskEntity);

        List<OrderItemVo> locks = lockVo.getLocks();
        List<SkuLockVo> skuLockVos = locks.stream()
                .map(orderItemVo -> {
                    SkuLockVo skuLockVo = new SkuLockVo();
                    List<Long> wareIds = baseMapper.listWareIdsHasStock(orderItemVo.getSkuId(), orderItemVo.getCount());
                    skuLockVo.setSkuId(orderItemVo.getSkuId());
                    skuLockVo.setNum(orderItemVo.getCount());
                    skuLockVo.setWareIds(wareIds);
                    return skuLockVo;
                }).collect(Collectors.toList());

        for (SkuLockVo skuLockVo : skuLockVos) {
            boolean lock = false;
            List<Long> wareIds = skuLockVo.getWareIds();
            Long skuId = skuLockVo.getSkuId();
            if (wareIds == null || wareIds.size() == 0) {
                throw new NoStockException(skuId);
            } else {
                for (Long wareId : wareIds) {

                    Long count = baseMapper.lockWareSku(skuId, skuLockVo.getNum(), wareId);
                    if (count == null || count == 0) {
                        lock = false;
                    } else {
                        //锁定成功，保存工作单详情
                        WareOrderTaskDetailEntity detailEntity = WareOrderTaskDetailEntity.builder()
                                .skuId(skuId)
                                .skuName("")
                                .skuNum(skuLockVo.getNum())
                                .taskId(taskEntity.getId())
                                .wareId(wareId)
                                .lockStatus(1).build();
                        wareOrderTaskDetailService.save(detailEntity);
                        //发送库存锁定消息至延迟队列
                        StockLockedTo lockedTo = new StockLockedTo();
                        lockedTo.setId(taskEntity.getId());
                        StockDetailTo detailTo = new StockDetailTo();
                        BeanUtils.copyProperties(detailEntity, detailTo);
                        lockedTo.setDetailTo(detailTo);

                        rabbitTemplate.convertAndSend("stock-event-exchange", "stock.locked", lockedTo);

                        lock = true;
                        break;
                    }
                }
                if (lock == false) {
                    throw new NoStockException(skuId);
                }
            }

        }
        return true;
    }
    /**
     * 1、没有这个订单，必须解锁库存
     * *          2、有这个订单，不一定解锁库存
     * *              订单状态：已取消：解锁库存
     * *                      已支付：不能解锁库存
     * 消息队列解锁库存
     *
     * @param stockLockedTo
     */
    @Override
    public void unlock(StockLockedTo stockLockedTo) {
        StockDetailTo detailTo = stockLockedTo.getDetailTo();
        WareOrderTaskDetailEntity detailEntity = wareOrderTaskDetailService.getById(detailTo.getId());
        //1.如果工作单详情不为空，说明该库存锁定成功
        if (detailEntity != null) {
            WareOrderTaskEntity taskEntity = wareOrderTaskService.getById(stockLockedTo.getId());
            R r = orderFeignService.infoByOrderSn(taskEntity.getOrderSn());
            if (r.getCode() == 0) {
                OrderTo order = r.getData("order", new TypeReference<OrderTo>() {
                });
                //没有这个订单||订单状态已经取消 解锁库存
                if (order == null || order.getStatus() == OrderStatusEnum.CANCLED.getCode()) {
                    //为保证幂等性，只有当工作单详情处于被锁定的情况下才进行解锁
                    if (detailEntity.getLockStatus() == WareTaskStatusEnum.Locked.getCode()) {
                        unlockStock(detailTo.getSkuId(), detailTo.getSkuNum(), detailTo.getWareId(), detailEntity.getId());
                    }
                }

            } else {
                throw new RuntimeException("远程调用订单服务失败");
            }
        } else {
            //无需解锁
        }
    }

    @Override
    public void unlock(OrderTo orderTo) {
        //为防止重复解锁，需要重新查询工作单
        String orderSn = orderTo.getOrderSn();
        WareOrderTaskEntity taskEntity = wareOrderTaskService.getBaseMapper().selectOne((new QueryWrapper<WareOrderTaskEntity>().eq("order_sn", orderSn)));
        //查询出当前订单相关的且处于锁定状态的工作单详情
        List<WareOrderTaskDetailEntity> lockDetails = wareOrderTaskDetailService.list(new QueryWrapper<WareOrderTaskDetailEntity>().eq("task_id", taskEntity.getId()).eq("lock_status", WareTaskStatusEnum.Locked.getCode()));
        for (WareOrderTaskDetailEntity lockDetail : lockDetails) {
            unlockStock(lockDetail.getSkuId(), lockDetail.getSkuNum(), lockDetail.getWareId(), lockDetail.getId());
        }
    }

    private void unlockStock(Long skuId, Integer skuNum, Long wareId, Long detailId) {
        //数据库中解锁库存数据
        baseMapper.unlockStock(skuId, skuNum, wareId);
        //更新库存工作单详情的状态
        WareOrderTaskDetailEntity detail = WareOrderTaskDetailEntity.builder()
                .id(detailId)
                .lockStatus(2).build();
        wareOrderTaskDetailService.updateById(detail);
    }



    @Data
    class SkuLockVo {
        private Long skuId;
        private Integer num;
        private List<Long> wareIds;
    }
}
/**
 * orderLockStock:
 * 步骤1：创建库存工作单
 * 输入: wareSkuLockVo.orderSn
 * ↓
 * 创建: WareOrderTaskEntity
 * ├── orderSn: "订单号"
 * ├── createTime: "当前时间"
 * └── id: "工作单ID"
 * 步骤2：转换商品信息
 * 输入: List<OrderItemVo>
 * ├── skuId: 1001
 * └── count: 2
 * ↓
 * 转换: List<SkuLockVo>
 * ├── skuId: 1001
 * ├── num: 2
 * └── wareIds: [1, 3, 5]  // 有库存的仓库ID
 * 步骤3：查询可用仓库
 * -- 对每个商品执行
 * SELECT ware_id FROM wms_ware_sku
 * WHERE sku_id = #{skuId} AND stock - locked_stock >= #{count}
 * 步骤4：锁定库存
 * 遍历: 每个SkuLockVo
 * ├── 遍历: 每个wareId
 * │   ├── 执行: UPDATE wms_ware_sku
 * │   │   SET locked_stock = locked_stock + #{num}
 * │   │   WHERE sku_id = #{skuId} AND ware_id = #{wareId}
 * │   │   AND stock - locked_stock >= #{num}
 * │   └── 判断: 返回影响行数
 * │       ├── 1: 锁定成功
 * │       └── 0: 库存不足
 * 步骤5：记录锁定详情
 * 锁定成功时:
 * 创建: WareOrderTaskDetailEntity
 * ├── taskId: "工作单ID"
 * ├── skuId: "商品ID"
 * ├── wareId: "仓库ID"
 * ├── skuNum: "锁定数量"
 * └── lockStatus: 1  // 已锁定
 * 步骤6：发送延迟消息
 * 构建: StockLockedTo
 * ├── id: "工作单ID"
 * └── detailTo: StockDetailTo
 * ├── skuId: "商品ID"
 * ├── wareId: "仓库ID"
 * └── skuNum: "锁定数量"
 * ↓
 * 发送: RabbitMQ
 * ├── Exchange: "stock-event-exchange"
 * ├── RoutingKey: "stock.locked"
 * └── 延迟时间: 30分钟
 */
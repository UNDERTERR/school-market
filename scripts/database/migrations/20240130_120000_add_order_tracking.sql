-- Migration: Add order tracking table
-- Created: 2024-01-30 12:00:00
-- Version: 20240130_120000
-- Description: 为现有数据库添加订单跟踪表

-- 创建订单跟踪表（如果不存在）
CREATE TABLE IF NOT EXISTS `oms_order_tracking` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '跟踪ID',
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `order_sn` varchar(64) NOT NULL COMMENT '订单号',
  `status` tinyint(1) NOT NULL COMMENT '状态',
  `description` varchar(500) DEFAULT NULL COMMENT '描述',
  `operator` varchar(100) DEFAULT NULL COMMENT '操作人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_tracking_order` (`order_id`),
  KEY `idx_order_tracking_sn` (`order_sn`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单跟踪表';

-- 添加外键约束（如果order表存在）
SET @fk_exists = 0;
SELECT COUNT(*) INTO @fk_exists 
FROM information_schema.table_constraints 
WHERE constraint_schema = DATABASE() 
AND constraint_name = 'fk_order_tracking_order' 
AND table_name = 'oms_order_tracking';

SET @add_fk = IF(@fk_exists = 0, 
    'ALTER TABLE oms_order_tracking ADD CONSTRAINT fk_order_tracking_order FOREIGN KEY (order_id) REFERENCES oms_order (id) ON DELETE CASCADE;',
    'SELECT "Foreign key already exists";');

PREPARE stmt FROM @add_fk;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 插入现有订单的历史跟踪数据（可选）
INSERT IGNORE INTO oms_order_tracking (order_id, order_sn, status, description, operator, create_time)
SELECT 
    id,
    order_sn,
    CASE status
        WHEN 0 THEN 1  -- 待付款 -> 订单创建
        WHEN 1 THEN 2  -- 待发货 -> 订单确认
        WHEN 2 THEN 3  -- 已发货 -> 订单发货
        WHEN 3 THEN 4  -- 已完成 -> 订单完成
        WHEN 4 THEN 5  -- 已关闭 -> 订单关闭
        ELSE 1
    END,
    CASE status
        WHEN 0 THEN '订单创建成功'
        WHEN 1 THEN '订单确认'
        WHEN 2 THEN '商品已发货'
        WHEN 3 THEN '订单完成'
        WHEN 4 THEN '订单已关闭'
        ELSE '未知状态'
    END,
    'system',
    COALESCE(update_time, create_time, NOW())
FROM oms_order
WHERE deleted = 0 AND id IS NOT NULL;
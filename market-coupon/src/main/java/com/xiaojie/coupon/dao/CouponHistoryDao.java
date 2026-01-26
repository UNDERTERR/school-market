package com.xiaojie.coupon.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaojie.coupon.entity.CouponHistoryEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券领取历史记录
 */
@Mapper
public interface CouponHistoryDao extends BaseMapper<CouponHistoryEntity> {
	
}

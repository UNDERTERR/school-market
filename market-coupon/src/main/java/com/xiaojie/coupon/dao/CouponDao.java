package com.xiaojie.coupon.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaojie.coupon.entity.CouponEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息

 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}

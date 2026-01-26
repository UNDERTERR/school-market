package com.xiaojie.coupon.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaojie.coupon.entity.SeckillSessionEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 秒杀活动场次

 */
@Mapper
public interface SeckillSessionDao extends BaseMapper<SeckillSessionEntity> {
	
}

package com.xiaojie.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaojie.common.utils.PageUtils;
import com.xiaojie.member.entity.MemberStatisticsInfoEntity;

import java.util.Map;

/**
 * 会员统计信息

 */
public interface MemberStatisticsInfoService extends IService<MemberStatisticsInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);
}


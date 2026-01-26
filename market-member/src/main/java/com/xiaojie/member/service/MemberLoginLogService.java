package com.xiaojie.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaojie.common.utils.PageUtils;
import com.xiaojie.member.entity.MemberLoginLogEntity;

import java.util.Map;

/**
 * 会员登录记录

 */
public interface MemberLoginLogService extends IService<MemberLoginLogEntity> {

    PageUtils queryPage(Map<String, Object> params);
}


package com.xiaojie.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaojie.common.utils.PageUtils;
import com.xiaojie.common.utils.Query;
import com.xiaojie.member.dao.MemberLoginLogDao;
import com.xiaojie.member.entity.MemberLoginLogEntity;
import com.xiaojie.member.service.MemberLoginLogService;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("memberLoginLogService")
public class MemberLoginLogServiceImpl extends ServiceImpl<MemberLoginLogDao, MemberLoginLogEntity> implements MemberLoginLogService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberLoginLogEntity> page = this.page(
                new Query<MemberLoginLogEntity>().getPage(params),
                new QueryWrapper<MemberLoginLogEntity>()
        );

        return new PageUtils(page);
    }

}
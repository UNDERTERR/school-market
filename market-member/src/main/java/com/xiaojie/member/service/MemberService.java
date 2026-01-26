package com.xiaojie.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaojie.common.utils.PageUtils;
import com.xiaojie.member.entity.MemberEntity;
import com.xiaojie.member.vo.MemberLoginVo;
import com.xiaojie.member.vo.MemberRegisterVo;
import com.xiaojie.member.vo.SocialUser;

import java.util.Map;

/**
 * 会员
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void register(MemberRegisterVo registerVo);

    MemberEntity login(MemberLoginVo loginVo);

    MemberEntity login(SocialUser socialUser);
}


package com.xiaojie.auto.feign;

import com.xiaojie.common.utils.R;
import com.xiaojie.auto.feign.fallback.MemberFallbackService;
import com.xiaojie.auto.vo.SocialUser;
import com.xiaojie.auto.vo.UserLoginVo;
import com.xiaojie.auto.vo.UserRegisterVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(value = "market-member",fallback = MemberFallbackService.class)
public interface MemberFeignService {

    @RequestMapping("member/member/register")
    R register(@RequestBody UserRegisterVo registerVo);


    @RequestMapping("member/member/login")
     R login(@RequestBody UserLoginVo loginVo);

    @RequestMapping("member/member/oauth2/login")
    R login(@RequestBody SocialUser socialUser);
}

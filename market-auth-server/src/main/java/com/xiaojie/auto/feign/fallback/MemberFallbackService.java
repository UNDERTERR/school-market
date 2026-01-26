package com.xiaojie.auto.feign.fallback;

import com.xiaojie.common.exception.BizCodeEnum;
import com.xiaojie.common.utils.R;
import com.xiaojie.auto.feign.MemberFeignService;
import com.xiaojie.auto.vo.SocialUser;
import com.xiaojie.auto.vo.UserLoginVo;
import com.xiaojie.auto.vo.UserRegisterVo;
import org.springframework.stereotype.Service;

@Service
public class MemberFallbackService implements MemberFeignService {
    @Override
    public R register(UserRegisterVo registerVo) {
        return R.error(BizCodeEnum.READ_TIME_OUT_EXCEPTION.getCode(), BizCodeEnum.READ_TIME_OUT_EXCEPTION.getMsg());
    }

    @Override
    public R login(UserLoginVo loginVo) {
        return R.error(BizCodeEnum.READ_TIME_OUT_EXCEPTION.getCode(), BizCodeEnum.READ_TIME_OUT_EXCEPTION.getMsg());
    }

    @Override
    public R login(SocialUser socialUser) {
        return R.error(BizCodeEnum.READ_TIME_OUT_EXCEPTION.getCode(), BizCodeEnum.READ_TIME_OUT_EXCEPTION.getMsg());
    }
}

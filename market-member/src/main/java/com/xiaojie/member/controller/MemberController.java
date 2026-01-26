package com.xiaojie.member.controller;

import com.xiaojie.common.exception.BizCodeEnum;
import com.xiaojie.common.utils.PageUtils;
import com.xiaojie.common.utils.R;
import com.xiaojie.member.entity.MemberEntity;
import com.xiaojie.member.exception.PhoneNumExistException;
import com.xiaojie.member.exception.UserExistException;
import com.xiaojie.member.feign.CouponFeignService;
import com.xiaojie.member.service.MemberService;
import com.xiaojie.member.vo.MemberLoginVo;
import com.xiaojie.member.vo.MemberRegisterVo;
import com.xiaojie.member.vo.SocialUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;



/**
 * 会员

 */
@RestController
@RequestMapping("member/member")
public class MemberController {
    @Autowired
    private MemberService memberService;

    @Autowired
    private CouponFeignService couponFeignService;


    @PostMapping("/login")
    public R login(@RequestBody MemberLoginVo loginVo) {
        MemberEntity entity=memberService.login(loginVo);
        if (entity!=null){
            return R.ok().put("memberEntity",entity);
        }else {
            return R.error(BizCodeEnum.LOGINACCT_PASSWORD_EXCEPTION.getCode(), BizCodeEnum.LOGINACCT_PASSWORD_EXCEPTION.getMsg());
        }
    }

    @PostMapping("/oauth2/login")
    public R login(@RequestBody SocialUser socialUser) {
        MemberEntity entity=memberService.login(socialUser);
        if (entity!=null){
            return R.ok().put("memberEntity",entity);
        }else {
            return R.error();
        }
    }

    /**
     * 注册会员
     * @return
     */
    @PostMapping("/register")
    public R register(@RequestBody MemberRegisterVo registerVo) {
        try {
            memberService.register(registerVo);
        } catch (UserExistException userException) {
            return R.error(BizCodeEnum.USER_EXIST_EXCEPTION.getCode(), BizCodeEnum.USER_EXIST_EXCEPTION.getMsg());
        } catch (PhoneNumExistException phoneException) {
            return R.error(BizCodeEnum.PHONE_EXIST_EXCEPTION.getCode(), BizCodeEnum.PHONE_EXIST_EXCEPTION.getMsg());
        }
        return R.ok();
    }

    @RequestMapping("/coupons")
    public R test(){
        MemberEntity memberEntity=new MemberEntity();
        memberEntity.setNickname("zhangsan");
        R memberCoupons = couponFeignService.memberCoupons();

        return memberCoupons.put("member",memberEntity).put("coupons",memberCoupons.get("coupons"));
    }

    /**
     * 列表
     */
    @GetMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = memberService.queryPage(params);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @GetMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		MemberEntity member = memberService.getById(id);

        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    public R save(@RequestBody MemberEntity member){
		memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    public R update(@RequestBody MemberEntity member){
		memberService.updateById(member);

        return R.ok();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}

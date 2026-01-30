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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;


/**
 * 会员
 */
@RestController
@RequestMapping("member/member")
@Tag(name = "会员管理", description = "会员相关接口")
public class MemberController {
    @Autowired
    private MemberService memberService;

    @Autowired
    private CouponFeignService couponFeignService;


    @PostMapping("/login")
    @Operation(summary = "会员登录", description = "用户账号密码登录")
    public R login(@Parameter(description = "登录信息") @RequestBody MemberLoginVo loginVo) {
        MemberEntity entity = memberService.login(loginVo);
        if (entity != null) {
            return R.ok().put("memberEntity", entity);
        } else {
            return R.error(BizCodeEnum.LOGINACCT_PASSWORD_EXCEPTION.getCode(), BizCodeEnum.LOGINACCT_PASSWORD_EXCEPTION.getMsg());
        }
    }

    @PostMapping("/oauth2/login")
    @Operation(summary = "社交登录", description = "通过第三方社交平台登录")
    public R login(@Parameter(description = "社交用户信息") @RequestBody SocialUser socialUser) {
        MemberEntity entity = memberService.login(socialUser);
        if (entity != null) {
            return R.ok().put("memberEntity", entity);
        } else {
            return R.error();
        }
    }

    /**
     * 注册会员
     *
     * @return
     */
    @PostMapping("/register")
    @Operation(summary = "会员注册", description = "用户注册新会员账号")
    public R register(@Parameter(description = "注册信息") @RequestBody MemberRegisterVo registerVo) {
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
    @Operation(summary = "获取会员优惠券", description = "获取当前会员的优惠券列表")
    public R test() {
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setNickname("zhangsan");
        R memberCoupons = couponFeignService.memberCoupons();

        return memberCoupons.put("member", memberEntity).put("coupons", memberCoupons.get("coupons"));
    }

    /**
     * 列表
     */
    @GetMapping("/list")
    @Operation(summary = "分页查询会员", description = "根据条件分页查询会员列表")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = memberService.queryPage(params);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @GetMapping("/info/{id}")
    @Operation(summary = "获取会员详情", description = "根据会员ID获取会员详细信息")
    public R info(@PathVariable("id") Long id) {
        MemberEntity member = memberService.getById(id);

        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    @Operation(summary = "保存会员", description = "新增会员信息")
    public R save(@Parameter(description = "会员信息") @RequestBody MemberEntity member) {
        memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    @Operation(summary = "更新会员", description = "更新会员信息")
    public R update(@Parameter(description = "会员信息") @RequestBody MemberEntity member) {
        memberService.updateById(member);

        return R.ok();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete")
    @Operation(summary = "删除会员", description = "根据会员ID删除会员信息")
    public R delete(@Parameter(description = "会员ID数组") @RequestBody Long[] ids) {
        memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}

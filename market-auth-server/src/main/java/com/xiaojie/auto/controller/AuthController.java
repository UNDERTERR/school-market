package com.xiaojie.auto.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.xiaojie.auto.feign.MemberFeignService;
import com.xiaojie.auto.feign.ThirdPartFeignService;
import com.xiaojie.auto.vo.SocialUser;
import com.xiaojie.auto.vo.UserLoginVo;
import com.xiaojie.auto.vo.UserRegisterVo;
import com.xiaojie.common.constant.AuthServerConstant;
import com.xiaojie.common.exception.BizCodeEnum;
import com.xiaojie.common.utils.R;
import com.xiaojie.common.vo.MemberResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 认证控制器 - 提供纯API接口（简化版）
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private MemberFeignService memberFeignService;
    
    @Autowired
    private ThirdPartFeignService thirdPartFeignService;
    
    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 检查登录状态（替代原login.html页面逻辑）
     */
    @GetMapping("/status")
    public R checkLoginStatus(HttpSession session) {
        Object loginUser = session.getAttribute(AuthServerConstant.LOGIN_USER);
        if (loginUser != null) {
            return R.ok("已登录").setData(loginUser);
        } else {
            return R.error("未登录");
        }
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public R login(@RequestBody @Valid UserLoginVo loginVo, HttpSession session) {
        try {
            // 调用会员服务进行登录验证
            R loginResult = memberFeignService.login(loginVo);

            if (loginResult.getCode() == 0) {
                // 登录成功，将用户信息存入Session
                String jsonString = JSON.toJSONString(loginResult.get("memberEntity"));
                MemberResponseVo memberResponseVo = JSON.parseObject(jsonString,
                        new TypeReference<MemberResponseVo>() {
                        });

                session.setAttribute(AuthServerConstant.LOGIN_USER, memberResponseVo);
                session.setMaxInactiveInterval(30 * 60); // 30分钟过期

                return R.ok("登录成功").setData(memberResponseVo);
            } else {
                return R.error((String) loginResult.get("msg"));
            }
        } catch (Exception e) {
            return R.error("登录失败：" + e.getMessage());
        }
    }

    /**
     * 发送短信验证码
     */
    @GetMapping("/sms/sendCode")
    public R sendCode(@RequestParam("phone") String phone) {
        try {
            // 接口防刷，在redis中缓存phone-code
            ValueOperations<String, String> ops = redisTemplate.opsForValue();
            String prePhone = AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone;
            String v = ops.get(prePhone);

            if (!StringUtils.isEmpty(v)) {
                long pre = Long.parseLong(v.split("_")[1]);
                // 如果存储的时间小于60s，说明60s内发送过验证码
                if (System.currentTimeMillis() - pre < 60000) {
                    return R.error(BizCodeEnum.SMS_CODE_EXCEPTION.getCode(), BizCodeEnum.SMS_CODE_EXCEPTION.getMsg());
                }
            }

            // 如果存在的话，删除之前的验证码
            redisTemplate.delete(prePhone);

            // 获取到6位数字的验证码
            String code = String.valueOf((int) ((Math.random() + 1) * 100000));

            // 在redis中进行存储并设置过期时间
            ops.set(prePhone, code + "_" + System.currentTimeMillis(), 10, TimeUnit.MINUTES);

            // 发送短信
            thirdPartFeignService.sendCode(phone, code);

            return R.ok("验证码发送成功");
        } catch (Exception e) {
            return R.error("验证码发送失败：" + e.getMessage());
        }
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public R register(@Valid @RequestBody UserRegisterVo registerVo, BindingResult result) {
        try {
            Map<String, String> errors = new HashMap<>();

            // 1. 判断校验是否通过
            if (result.hasErrors()) {
                // 1.1 如果校验不通过，则封装校验结果
                result.getFieldErrors().forEach(item -> {
                    errors.put(item.getField(), item.getDefaultMessage());
                });
                return R.error("参数验证失败").setData(errors);
            }

            // 2. 判断验证码是否正确
            String codeKey = AuthServerConstant.SMS_CODE_CACHE_PREFIX + registerVo.getPhone();
            String code = redisTemplate.opsForValue().get(codeKey);

            // 2.1 如果对应手机的验证码不为空且与提交上的相等-》验证码正确
            if (!StringUtils.isEmpty(code) && registerVo.getCode().equals(code.split("_")[0])) {
                // 2.1.1 使得验证后的验证码失效
                redisTemplate.delete(codeKey);

                // 2.1.2 远程调用会员服务注册
                R r = memberFeignService.register(registerVo);
                if (r.getCode() == 0) {
                    return R.ok("注册成功");
                } else {
                    // 调用失败，返回注册页并显示错误信息
                    String msg = (String) r.get("msg");
                    errors.put("msg", msg);
                    return R.error("注册失败").setData(errors);
                }
            } else {
                // 2.2 验证码错误
                errors.put("code", "验证码错误");
                return R.error("验证码错误").setData(errors);
            }
        } catch (Exception e) {
            return R.error("注册失败：" + e.getMessage());
        }
    }




    /**
     * 社交登录
     */
    @PostMapping("/social/login")
    public R socialLogin(@RequestBody @Valid SocialUser socialUser, HttpSession session) {
        try {
            // 调用会员服务进行社交登录
            R loginResult = memberFeignService.login(socialUser);
            if (loginResult.getCode() == 0) {
                // 登录成功，将用户信息存入Session
                String jsonString = JSON.toJSONString(loginResult.get("memberEntity"));
                MemberResponseVo memberResponseVo = JSON.parseObject(jsonString,
                        new TypeReference<MemberResponseVo>() {
                        });
                session.setAttribute(AuthServerConstant.LOGIN_USER, memberResponseVo);
                session.setMaxInactiveInterval(30 * 60);
                return R.ok("社交登录成功").setData(memberResponseVo);
            } else {
                return R.error((String) loginResult.get("msg"));
            }
        } catch (Exception e) {
            return R.error("社交登录失败：" + e.getMessage());
        }
    }


    /**
     * 退出登录
     */
    @PostMapping("/logout")
    public R logout(HttpSession session) {
        try {
            session.invalidate(); // 清除Session
            return R.ok("退出成功");
        } catch (Exception e) {
            return R.error("退出失败：" + e.getMessage());
        }
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/info")
    public R getUserInfo(HttpSession session) {
        try {
            Object loginUser = session.getAttribute(AuthServerConstant.LOGIN_USER);
            if (loginUser == null) {
                return R.error("用户未登录");
            }
            return R.ok().setData(loginUser);
        } catch (Exception e) {
            return R.error("获取用户信息失败：" + e.getMessage());
        }
    }
}
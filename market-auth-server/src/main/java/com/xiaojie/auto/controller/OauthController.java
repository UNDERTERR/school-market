package com.xiaojie.auto.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.xiaojie.auto.feign.MemberFeignService;
import com.xiaojie.auto.vo.SocialUser;
import com.xiaojie.common.constant.AuthServerConstant;
import com.xiaojie.common.utils.HttpUtils;
import com.xiaojie.common.utils.R;
import com.xiaojie.common.vo.MemberResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@RestController
@RequestMapping("/oauth2.0/weibo")
public class OauthController {

    @Autowired
    private MemberFeignService memberFeignService;

    private String clientId;

    private String clientSecret;

    private String redirectUri;

    /**
     * 微博回调 - API 版本
     * 返回登录结果，前端根据 code 决定跳转
     */
    @GetMapping("/success")
    public R authorize(@RequestParam("code") String code, HttpSession session) {

        Map<String, String> query = new HashMap<>();
        //TODO 换了 weibo api
        query.put("client_id", clientId);
        query.put("client_secret", clientSecret);
        query.put("grant_type", "authorization_code");
        query.put("redirect_uri", redirectUri);
        query.put("code", code);

        try {
            HttpResponse response = HttpUtils.doPost(
                    "https://api.weibo.com",
                    "/oauth2/access_token",
                    "post",
                    new HashMap<>(),
                    query,
                    new HashMap<>()
            );

            if (response.getStatusLine().getStatusCode() != 200) {
                return R.error(400, "获得第三方授权失败").put("redirect", "/login.html");
            }

            String json = EntityUtils.toString(response.getEntity());
            SocialUser socialUser = JSON.parseObject(json, new TypeReference<SocialUser>() {});
            R loginResult = memberFeignService.login(socialUser);

            if (loginResult.getCode() != 0) {
                return R.error(500, "登录失败").put("redirect", "/login.html");
            }

            // 写入 Session
            String memberJson = JSON.toJSONString(loginResult.get("memberEntity"));
            MemberResponseVo member = JSON.parseObject(memberJson, new TypeReference<MemberResponseVo>() {});
            session.setAttribute(AuthServerConstant.LOGIN_USER, member);

            // 返回成功 + 重定向地址
            return R.ok().put("redirect", "/").put("member", member);

        } catch (Exception e) {
            log.error("微博登录异常", e);
            return R.error(500, "系统异常").put("redirect", "/login.html");
        }
    }
}
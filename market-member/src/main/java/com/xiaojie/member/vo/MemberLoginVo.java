package com.xiaojie.member.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "会员登录请求参数")
public class MemberLoginVo {
    @Schema(description = "登录账号", example = "test@example.com")
    private String loginAccount;
    
    @Schema(description = "登录密码", example = "123456")
    private String password;
}

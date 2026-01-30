package com.xiaojie.member.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
@Schema(description = "会员注册请求参数")
public class MemberRegisterVo {
    @Schema(description = "用户名", example = "testuser")
    private String userName;

    @Schema(description = "密码", example = "123456")
    private String password;

    @Schema(description = "手机号", example = "13800138000")
    private String phone;
}

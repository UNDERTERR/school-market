package com.xiaojie.member.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 会员

 */
@Data
@TableName("ums_member")
@Schema(description = "会员实体")
public class MemberEntity implements Serializable {
	private static final long serialVersionUID = 1L;

/**
	 * id
	 */
	@TableId
	@Schema(description = "会员ID", example = "1")
	private Long id;
	/**
	 * 会员等级id
	 */
	@Schema(description = "会员等级ID", example = "1")
	private Long levelId;
	/**
	 * 用户名
	 */
	@Schema(description = "用户名", example = "testuser")
	private String username;
	/**
	 * 密码
	 */
	@Schema(description = "密码")
	private String password;
	/**
	 * 昵称
	 */
	@Schema(description = "昵称", example = "测试用户")
	private String nickname;
	/**
	 * 手机号码
	 */
	@Schema(description = "手机号码", example = "13800138000")
	private String mobile;
	/**
	 * 邮箱
	 */
	@Schema(description = "邮箱", example = "test@example.com")
	private String email;
	/**
	 * 头像
	 */
	@Schema(description = "头像URL", example = "https://example.com/avatar.jpg")
	private String header;
	/**
	 * 性别
	 */
	private Integer gender;
	/**
	 * 生日
	 */
	private Date birth;
	/**
	 * 所在城市
	 */
	private String city;
	/**
	 * 职业
	 */
	private String job;
	/**
	 * 个性签名
	 */
	private String sign;
	/**
	 * 用户来源
	 */
	private Integer sourceType;
	/**
	 * 积分
	 */
	private Integer integration;
	/**
	 * 成长值
	 */
	private Integer growth;
	/**
	 * 启用状态
	 */
	private Integer status;
	/**
	 * 注册时间
	 */
	private Date createTime;


	/**
	 * 社交登录UID
	 */
	private String uid;

	/**
	 * 社交登录TOKEN
	 */
	private String accessToken;

	/**
	 * 社交登录过期时间
	 */
	private long expiresIn;
}

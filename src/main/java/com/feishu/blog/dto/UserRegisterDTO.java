package com.feishu.blog.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserRegisterDTO {

    /** 昵称：2-20 个中英文或下划线 */
    @JsonProperty("username")
    @NotBlank
    @Pattern(regexp = "^[\\w\\u4e00-\\u9fa5]{2,20}$", message = "昵称应为 2-20 位中英文或下划线")
    private String username;

    /** 邮箱 */
    @JsonProperty("email")
    @Email(message = "邮箱格式不正确")
    private String email;

    /** 手机号：国内 11 位 */
    @JsonProperty("phone")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    /** 密码：6-16 位，且必须包含小写字母、大写字母和数字的字符串 */
    @JsonProperty("password")
    @NotBlank
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d]{6,16}$",
            message = "密码需 6-16 位，含大小写字母和数字")
    private String password;

    /** 6位邀请码 */
    @JsonProperty("invite_code")
    @Pattern(regexp = "^[0-9A-Za-z]{6}$",
            message = "邀请码需6位，含大小写字母和数字")
    private String inviteCode;

    @JsonProperty("captcha_id")
    private String captchaId;

    @JsonProperty("captcha_code")
    private Integer captchaCode;
}

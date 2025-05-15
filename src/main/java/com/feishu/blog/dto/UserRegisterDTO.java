package com.feishu.blog.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserRegisterDTO {

    /** 昵称：2-20 个中英文或下划线 */
    @NotBlank
    @Pattern(regexp = "^[\\w\\u4e00-\\u9fa5]{2,20}$", message = "昵称应为 2-20 位中英文或下划线")
    private String name;

    /** 邮箱 */
    @NotBlank
    @Email(message = "邮箱格式不正确")
    private String email;

    /** 手机号：国内 11 位 */
    @NotBlank
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    /** 密码：6-16 位，含大小写和数字 */
    @NotBlank
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d]{6,16}$",
            message = "密码需 6-16 位，含大小写字母和数字")
    private String passwd;
}

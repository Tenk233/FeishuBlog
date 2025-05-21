package com.feishu.blog.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserLoginDTO {
    /** 昵称：2-20 个中英文或下划线 */
    @JsonProperty("username")
    @NotBlank
    @Pattern(regexp = "^[\\w\\u4e00-\\u9fa5]{2,20}$", message = "昵称应为 2-20 位中英文或下划线")
    private String username;

    /** 密码：6-16 位，且必须包含小写字母、大写字母和数字的字符串 */
    @JsonProperty("password")
    @NotBlank
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d]{6,16}$",
            message = "密码需 6-16 位，含大小写字母和数字")
    private String password;
}

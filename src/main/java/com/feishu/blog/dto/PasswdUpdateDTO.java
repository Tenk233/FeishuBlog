package com.feishu.blog.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PasswdUpdateDTO {
    @NotNull
    private Integer id;

    @NotNull
    @JsonProperty("originalPassword")
    private String oldPasswd;

    @JsonProperty("newPassword")
    private String newPasswd;

    @JsonProperty("logout")
    private Boolean logout;
}

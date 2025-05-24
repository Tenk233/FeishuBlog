package com.feishu.blog.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NonNull;

/**
 * description:
 *
 * @author Tenk
 */
@Data
public class ModifyUserDTO {
    private Integer id;

    private String username;

    private String phone;
    private String email;

    private String password;

    @JsonProperty("avatar_url")
    private String avatarUrl;

    @JsonProperty("is_block")
    private Boolean isBlock;

    @JsonProperty("abstract")
    private String userAbstract;
}

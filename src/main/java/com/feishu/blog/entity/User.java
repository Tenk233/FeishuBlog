package com.feishu.blog.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * tb_user
 * @author 
 */
@Getter
@Setter
public class User implements Serializable {

    public static final int ROLE_USER = 0;
    public static final int ROLE_ADMIN = 1;

    private Integer id;

    private Integer role;

    private String username;

    @JsonProperty("password_hash")
    private String passwordHash;

    private String phone;

    private String email;

    private String salt;

    @JsonProperty("create_time")
    private Date createTime;

    @JsonProperty("avatar_url")
    private String avatarUrl;

    @JsonProperty("is_blocked")
    private Boolean isBlocked;

    @JsonProperty("abstract")
    private String userAbstract;

    @Serial
    private static final long serialVersionUID = 1L;
}
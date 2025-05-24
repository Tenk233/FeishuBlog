package com.feishu.blog.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.feishu.blog.entity.User;
import lombok.Data;

import java.util.Date;

/**
 * description:
 *
 * @author Tenk
 * @date 2025/5/20
 */
//@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class UserInfoVO {

    public UserInfoVO() {}
    public UserInfoVO(User user, Boolean isLogin) {
        this.id = user.getId();
        this.role = user.getRole();
        this.username = user.getUsername();
        this.passwordHash = user.getPasswordHash();

        this.phone = user.getPhone();
        this.email = user.getEmail();
        this.avatarUrl = user.getAvatarUrl();
        this.userAbstract = user.getUserAbstract();
        this.createTime = user.getCreateTime();

        this.isBlocked = user.getIsBlocked();

        this.isLogin = isLogin;
    }

    private Integer id;

    private Integer role;

    private String username;

    @JsonProperty("password_hash")
    private String passwordHash;

    private String phone;

    private String email;

    @JsonIgnore
    private Date createTime;

    /** 前端真正接收的更新时间（秒） */
    @JsonProperty("create_time")
    public long getCreateTimeEpoch() {
        return createTime == null ? 0L : createTime.getTime();
    }

    @JsonProperty("avatar_url")
    private String avatarUrl;

    @JsonProperty("is_blocked")
    private Boolean isBlocked;

    @JsonProperty("is_login")
    private Boolean isLogin;

    @JsonProperty("abstract")
    private String userAbstract;
}

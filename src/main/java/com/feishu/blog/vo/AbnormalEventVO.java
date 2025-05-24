package com.feishu.blog.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.feishu.blog.entity.AbnormalEvent;
import lombok.Data;

/**
 * description:
 *
 * @author Tenk
 * @date 2025/5/24
 */
@Data
public class AbnormalEventVO {
    @JsonProperty("user_id")
    private Integer userId;

    @JsonProperty("username")
    private String username;

    @JsonProperty("detail")
    private String reason;

    public AbnormalEventVO(Integer userId, String username, String reason) {
        this.userId = userId;
        this.username = username;
        this.reason = reason;
    }
}

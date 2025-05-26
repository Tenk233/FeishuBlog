package com.feishu.blog.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

/**
 * description:
 *
 * @author Tenk
 * @date 2025/5/24
 */
@Data
public class AbnormalEvent {
    public static final int EVENT_ABNORMAL_LOGIN = 0;
    public static final int EVENT_ABNORMAL_BLOG = 1;

    private Integer id;
    private Integer type;
    @JsonProperty("user_id")
    private Integer userId;
    @JsonProperty("blog_id")
    private Integer blogId;
    @JsonIgnore
    private Date createTime;

    @JsonProperty("create_time")
    public long getCreateTime() {
        return createTime == null ? 0L : createTime.getTime();
    }

    private String reason;

    public static AbnormalEvent generateLoginEvent(Integer userId, String reason) {
        AbnormalEvent abnormalEvent = new AbnormalEvent();
        abnormalEvent.setType(EVENT_ABNORMAL_LOGIN);
        abnormalEvent.setUserId(userId);
        abnormalEvent.setReason(reason);
        return abnormalEvent;
    }

    public static AbnormalEvent generateBlogEvent(Integer userId, Integer blogId, String reason) {
        AbnormalEvent abnormalEvent = new AbnormalEvent();
        abnormalEvent.setType(EVENT_ABNORMAL_BLOG);
        abnormalEvent.setUserId(userId);
        abnormalEvent.setBlogId(blogId);
        abnormalEvent.setReason(reason);
        return abnormalEvent;
    }
}

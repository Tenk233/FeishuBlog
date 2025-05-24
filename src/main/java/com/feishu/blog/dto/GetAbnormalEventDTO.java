package com.feishu.blog.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;

import java.util.Date;

/**
 * description:
 *
 * @author Tenk
 * @date 2025/5/24
 */
@Data
public class GetAbnormalEventDTO {
    @Min(value = 1, message = "page 必须大于 0")
    private Integer page;
    @Min(value = 1, message = "limit 必须大于 0")
    private Integer limit;


    private Integer type;
    private Integer userId;
    private Integer blogId;
    private Date after;

    public void setUser_id(Integer user_id) {
        this.userId = user_id;
    }

    public void setBlog_id(Integer blogId) {
        this.blogId = blogId;
    }
}

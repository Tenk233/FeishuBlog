package com.feishu.blog.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * description:
 *
 * @author Tenk
 */
@Data
public class GetBlogListDTO {
    /** 页码，必须是正整数（>=1） */
    @Min(value = 1, message = "page 必须大于 0")
    private Integer page;

    /** 每页条数，必须大于 0 */
    @Min(value = 1, message = "limit 必须大于 0")
    private Integer limit;

    private String tag;

    @JsonProperty("order_by")
    @Pattern(regexp = "^(time|likes)$",
            message = "order_by 仅支持 time 或 likes")
    private String orderBy;

    @JsonProperty("sort_order")
    @Pattern(regexp = "^(?i)(asc|desc)$",
            message = "sort_order 仅支持 asc 或 desc")
    private String sortOrder;

    private String keyword;

    @JsonProperty("user_id")
    private Integer userId;

    /* —— 兼容下划线参数 —— */
    public void setOrder_by(String orderBy)   { this.orderBy  = orderBy; }
    public void setSort_order(String sortOrder){ this.sortOrder = sortOrder; }
    public void setUser_id(Integer userId)    { this.userId   = userId;  }
}

package com.feishu.blog.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class GetUserListDTO {

    private String keyWord;

    /**
     * 只允许按这几个字段排序，前端传入时如果不是其中之一就校验失败
     * 这里示例允许 id, username, email, createdAt 四个值
     */
    @Pattern(
            regexp = "^(id|username|email)$",
            message = "orderBy 必须是 id、username 或 email 之一"
    )
    private String orderBy;

    /** 页码，必须是正整数（> 1） */
    @Min(value = 1, message = "page 必须大于 0")
    private Integer page;

    /** 每页条数，必须大于 0 */
//    @Min(value = 1, message = "limit 必须大于 1")
    private Integer limit;

    /**
     * 只能是 0（降序）或 1（升序）
     */
    @Min(value = 0, message = "asc 只能是 0（降序）或 1（升序）")
    @Max(value = 1, message = "asc 只能是 0（降序）或 1（升序）")
    private Integer asc;
}

package com.feishu.blog.dto;

import jakarta.validation.constraints.Min;
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


}

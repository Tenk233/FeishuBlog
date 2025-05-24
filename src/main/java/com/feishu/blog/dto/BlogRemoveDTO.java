package com.feishu.blog.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * description:
 *
 * @author Tenk
 * @date 2025/5/23
 */
@Data
public class BlogRemoveDTO {
    private Integer uid;
    @NotNull(message = "要下架的博客ID不能为空")
    private Integer bid;
    @NotNull(message = "下架原因不能为空")
    private String reason;
}

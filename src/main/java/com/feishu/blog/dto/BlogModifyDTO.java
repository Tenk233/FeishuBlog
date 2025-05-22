package com.feishu.blog.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * description:
 *
 * @author Tenk
 * @date 2025/5/21
 */
@Data
public class BlogModifyDTO {
    @NotNull(message = "id不能为空")
    @JsonProperty("article_id")
    private Integer id;

    @JsonProperty("article_header")
    private String title;

    @JsonProperty("article_detail")
    private String content;

    @JsonProperty("tags")
    private List<String> tags;

    @JsonProperty("cover_image")
    private String coverImageUri;

    @JsonProperty("status")
    private Integer status;
}

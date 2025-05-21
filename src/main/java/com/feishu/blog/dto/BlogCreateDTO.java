package com.feishu.blog.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class BlogCreateDTO {
    @JsonProperty("article_id")
    private Integer id;

    @NotNull
    @JsonProperty("article_header")
    private String title;

    @NotNull
    @JsonProperty("article_detail")
    private String content;

    @JsonProperty("tags")
    private List<String> tagIds;

    @JsonProperty("cover_image")
    private String coverImageUri;

    @JsonProperty("is_draft")
    private Boolean isDraft;
}

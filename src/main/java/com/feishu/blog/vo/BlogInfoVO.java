package com.feishu.blog.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.feishu.blog.entity.Blog;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class BlogInfoVO {
    private Integer id;
    private String title;
    private String content;

    @JsonProperty("author_id")
    private Integer authorId;

    @JsonProperty("author_name")
    private String authorName;

    @JsonProperty("create_time")
    private Date createTime;

    @JsonProperty("last_modified_time")
    private Date lastModified;

    @JsonProperty("cover_image")
    private String coverImageUri;

    @JsonProperty("status")
    private Integer status;

    private List<String> tags;

    public BlogInfoVO() {}

    public BlogInfoVO(Blog blog, String authorName, List<String> tags) {
        this.id = blog.getId();
        this.title = blog.getTitle();
        this.content = blog.getContent();
        this.authorId = blog.getAuthorId();
        this.authorName = authorName;
        this.createTime = blog.getCreateTime();
        this.lastModified = blog.getLastModified();
        this.coverImageUri = blog.getCoverImageUri();
        this.status = blog.getStatus();
        this.tags = tags;
    }
}

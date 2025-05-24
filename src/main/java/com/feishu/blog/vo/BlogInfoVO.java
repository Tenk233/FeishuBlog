package com.feishu.blog.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.feishu.blog.entity.Blog;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class BlogInfoVO {
    private Integer id;
    @JsonProperty("header")
    private String title;

    private List<String> tags;

    @JsonProperty("abstract")
    private String blogAbstract;

    @JsonProperty("like")
    private Integer likes;

    @JsonProperty("article_detail")
    private String content;

    @JsonProperty("author_id")
    private Integer authorId;

    @JsonProperty("author")
    private String authorName;

    @JsonIgnore
    private Date createTime;

    @JsonProperty("create_time")
    public long getCreateTime() {
        return createTime == null ? 0L : createTime.getTime();
    }

    /** 保留给后台业务用，不直接序列化 */
    @JsonIgnore
    private Date lastModified;

    /** 前端真正接收的更新时间（秒） */
    @JsonProperty("last_modified_date")
    public long getLastModifiedEpoch() {
        return lastModified == null ? 0L : lastModified.getTime();
    }

    @JsonProperty("cover_image")
    private String coverImageUri;

    @JsonProperty("status")
    private Integer status;


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
        this.likes = blog.getLikes();
        int len = blog.getContent().length();
        if (len >= 50) {
            len = 50;
        }
        this.blogAbstract = blog.getContent().substring(0, len - 1 );
    }
}

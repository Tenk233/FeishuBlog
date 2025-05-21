package com.feishu.blog.entity;

import lombok.Data;

import java.util.Date;

/**
 * description:
 *
 * @author Tenk
 * @date 2025/5/21
 */
@Data
public class Blog {
    public static final boolean STATUS_DRAFT = true;
    public static final boolean STATUS_PUBLISH = false;

    private Integer id;
    private String title;
    private String content;
    private Integer authorId;
    private Date createTime;
    private Date lastModified;
    private String coverImageUri;
    private Boolean isDraft;
}

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
    public static final int STATUS_DRAFT = 0;
    public static final int STATUS_FORBIDDEN = 1;
    public static final int STATUS_UNDER_VIEW = 2;
    public static final int STATUS_PUBLISHED = 3;

    private Integer id;
    private String title;
    private String content;
    private Integer authorId;
    private Date createTime;
    private Date lastModified;
    private String coverImageUri;
    private Integer status;
    private Integer likes;
}

package com.feishu.blog.entity;

import lombok.Data;

/**
 * description:
 *
 * @author Tenk
 * @date 2025/5/21
 */
@Data
public class BlogTag {
    private Integer id;
    private Integer blogId;
    private String tag;
}

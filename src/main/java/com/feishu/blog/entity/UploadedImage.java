package com.feishu.blog.entity;

import lombok.Data;

import java.util.Date;

/**
 * description:
 *
 * @author Tenk
 * @date 2025/5/25
 */
@Data
public class UploadedImage {

    public static final int STATUS_UNCHECK = 0;
    public static final int STATUS_PASSED = 1;

    private Integer id;
    private String name;
    private Integer userId;
    private Integer status;
    private Date uploadTime;

    public static UploadedImage generateImage(String name, Integer userId) {
        UploadedImage image = new UploadedImage();
        image.setName(name);
        image.setUserId(userId);
        return image;
    }
}

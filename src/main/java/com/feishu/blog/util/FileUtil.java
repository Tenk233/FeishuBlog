package com.feishu.blog.util;

import org.springframework.http.MediaType;

import java.util.Set;

public class FileUtil {
    /** 允许的 MIME 类型 */
    public static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
            MediaType.IMAGE_JPEG_VALUE,
            MediaType.IMAGE_PNG_VALUE,
            MediaType.IMAGE_GIF_VALUE
    );

    /* 保存目录（也可以放到配置文件注入） */
    public static final String IMAGE_SAVE_DIR = "z:/uploads/img";

    public static final String IMAGE_URI_PREFIX = "/api/img";
}

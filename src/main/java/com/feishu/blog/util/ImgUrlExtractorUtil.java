package com.feishu.blog.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ImgUrlExtractor —— 从字符串中抽取 /api/img/{image_name}
 *
 * image_name 允许的字符：
 *   - 英文字母、数字
 *   - 下划线、短横线、点
 */
public final class ImgUrlExtractorUtil {

    // 协议 http/https  + 任意主机端口  + 固定前缀 /api/img/  + 文件名 (捕获组1)
    private static final Pattern IMG_URL_PATTERN = Pattern.compile(
            "(?i)https?://[\\w.-]+(?::\\d+)?/api/img/([A-Za-z0-9_.-]+)");

    private ImgUrlExtractorUtil() { /* 工具类禁止实例化 */ }

    /**
     * 从原始字符串中匹配所有图片 URL，返回 image_name 列表
     * @param text 任意文本
     * @return image_name 集合（按出现顺序，无去重）
     */
    public static List<String> extractImageNames(String text) {
        List<String> result = new ArrayList<>();
        if (text == null || text.isBlank()) {
            return result;
        }
        Matcher m = IMG_URL_PATTERN.matcher(text);
        while (m.find()) {
            result.add(m.group(1));   // 捕获组 1 = image_name
        }
        return result;
    }
}

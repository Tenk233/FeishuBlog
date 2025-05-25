package com.feishu.blog.service.impl;


import com.feishu.blog.entity.Result;
import com.feishu.blog.entity.UploadedImage;
import com.feishu.blog.exception.BizException;
import com.feishu.blog.mapper.ImageMapper;
import com.feishu.blog.service.ImageService;
import com.feishu.blog.util.FileUtil;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Service             // 声明为 Bean
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    @Resource
    private ImageMapper imageMapper;

    @Override
    public String uploadImage(MultipartFile file, Integer userId) {
        /* ---------- 类型校验 ---------- */
        String ext = getExt(file);

        String newName = UUID.randomUUID() + "." + ext;

        try {
            Path dir = Paths.get(FileUtil.IMAGE_SAVE_DIR);
            /* ---------- 保存 ---------- */
            Files.createDirectories(dir);
            Path dest = dir.resolve(newName);
            file.transferTo(dest);

            // 插入数据库以备使用
            imageMapper.insertImage(UploadedImage.generateImage(newName, userId));

        } catch (IOException e) {
            log.warn("文件上传错误: {}", e.getMessage());
            throw new BizException(BizException.INTERNAL_ERROR, "文件上传错误");
        }
        String uri = FileUtil.IMAGE_URI_PREFIX + "/" + newName;
        log.debug("存储文件: {}", uri);
        return uri;
    }

    private static String getExt(MultipartFile file) {
        /* TODO: 做文件结构校验，而不只是文件扩展名 */
        String contentType = file.getContentType();
        if (contentType == null || !FileUtil.ALLOWED_IMAGE_TYPES.contains(contentType)) {
            throw new BizException(BizException.USER_WRONG_INPUT, "仅支持 jpg/png/gif");
        }

        /* ---------- 生成安全文件名 ---------- */
        return switch (contentType) {
            case MediaType.IMAGE_JPEG_VALUE -> "jpg";
            case MediaType.IMAGE_PNG_VALUE  -> "png";
            case MediaType.IMAGE_GIF_VALUE  -> "gif";
            default -> throw new BizException(BizException.USER_WRONG_INPUT, "仅支持 jpg/png/gif");
        };
    }
}

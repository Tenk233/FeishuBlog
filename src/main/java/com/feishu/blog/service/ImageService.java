package com.feishu.blog.service;


import com.feishu.blog.exception.BizException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageService {
    /**
     * 上传一个图片文件
     * @param multipartFile
     * @return 文件路径
     */
    String uploadImage(MultipartFile multipartFile, Integer userId);
}

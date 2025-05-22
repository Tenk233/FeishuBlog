package com.feishu.blog.controller;

import com.feishu.blog.entity.Result;
import com.feishu.blog.exception.BizException;
import com.feishu.blog.service.ImageService;
import com.feishu.blog.util.JwtUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/image")
public class ImageController {

    @Resource
    private ImageService imageService;

    @PutMapping(value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<?> upload(@RequestParam("image") MultipartFile file,
                            HttpServletRequest req) {
        Integer userId = (Integer)req.getAttribute(JwtUtil.ITEM_ID);
        String uri = imageService.uploadImage(file, userId);
        return Result.success(uri);
    }
}

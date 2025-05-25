package com.feishu.blog.controller;

import com.feishu.blog.dto.ImageClassificationDTO;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.feishu.blog.service.CozeImageService;

import java.io.IOException;

@RestController
//@RequiredArgsConstructor
@RequestMapping("/api/coze")
public class CozeController {
    @Resource
    private CozeImageService cozeService;

    @PostMapping("/imageCompliance")
    public ResponseEntity<ImageClassificationDTO> imageCompliance(@RequestParam("file") MultipartFile file) throws IOException {
        ImageClassificationDTO response = cozeService.processImageCompliance(file);
        return ResponseEntity.ok(response);
    }
}

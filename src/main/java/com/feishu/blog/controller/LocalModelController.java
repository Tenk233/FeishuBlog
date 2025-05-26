package com.feishu.blog.controller;

import com.feishu.blog.dto.ClassificationDTO;
import com.feishu.blog.service.LocalImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/local_model")
public class LocalModelController {
    @Autowired
    private LocalImageService service;

    @PostMapping("/imageCompliance")
    public ResponseEntity<ClassificationDTO> classifyImage(@RequestParam("file") MultipartFile file) throws Exception {
//        byte[] imageData = file.getBytes();
        ClassificationDTO response = service.classifyImage(file);
        return ResponseEntity.ok(response);
    }
}
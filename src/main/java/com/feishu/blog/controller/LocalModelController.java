package com.feishu.blog.controller;

import com.feishu.blog.dto.ImageClassificationDTO;
import com.feishu.blog.dto.SentimentDTO;
import com.feishu.blog.service.LocalImageService;
import com.feishu.blog.service.LocalSentimentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/local_model")
public class LocalModelController {
    @Autowired
    private LocalImageService service;
    @Autowired
    private LocalSentimentService sentimentService;

    @PostMapping("/imageCompliance")
    public ResponseEntity<ImageClassificationDTO> classifyImage(@RequestParam("file") MultipartFile file) throws Exception {
//        byte[] imageData = file.getBytes();
        ImageClassificationDTO response = service.classifyImage(file);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/contentCompliance")
    public ResponseEntity<SentimentDTO> contentCompliance(@RequestBody Map<String, String> request) {
        SentimentDTO response = sentimentService.processContentCompliance(request.get("text"));
        return ResponseEntity.ok(response);
    }
}
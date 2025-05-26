package com.feishu.blog.controller;

import com.feishu.blog.dto.ImageClassificationDTO;
import com.feishu.blog.dto.SentimentDTO;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.feishu.blog.service.CozeImageService;
import com.feishu.blog.service.CozeSentimentService;

import java.io.IOException;
import java.util.Map;

@RestController
//@RequiredArgsConstructor
@RequestMapping("/api/coze")
public class CozeController {
    @Resource
    private CozeImageService cozeService;
    @Autowired
    private CozeSentimentService cozeSentimentService;

    @PostMapping("/imageCompliance")
    public ResponseEntity<ImageClassificationDTO> imageCompliance(@RequestParam("file") MultipartFile file) throws IOException {
        ImageClassificationDTO response = cozeService.processImageCompliance(file);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/contentCompliance")
    public ResponseEntity<SentimentDTO> contentCompliance(@RequestBody Map<String, String> request) {
        SentimentDTO response =  cozeSentimentService.processContentCompliance(request.get("text"));
        return ResponseEntity.ok(response);
    }
}

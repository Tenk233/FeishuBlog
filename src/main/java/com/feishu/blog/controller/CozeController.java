package com.feishu.blog.controller;

import com.feishu.blog.dto.ClassificationDTO;
import com.feishu.blog.service.CozeTextService;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.feishu.blog.service.CozeImageService;

import java.io.IOException;
import java.util.Map;

@RestController
//@RequiredArgsConstructor
@RequestMapping("/api/coze")
public class CozeController {
    @Resource
    private CozeImageService cozeService;
    @Resource
    private CozeTextService cozeTextService;

    @PostMapping("/imageCompliance")
    public ResponseEntity<ClassificationDTO> imageCompliance(@RequestParam("file") MultipartFile file) throws IOException {
        ClassificationDTO response = cozeService.processImageCompliance(file);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/contentCompliance")
    public ResponseEntity<ClassificationDTO> contentCompliance(@RequestBody Map<String, String> request) {
        ClassificationDTO response =  cozeTextService.processContentCompliance(request.get("text"));
        return ResponseEntity.ok(response);
    }
}

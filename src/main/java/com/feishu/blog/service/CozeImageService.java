package com.feishu.blog.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.feishu.blog.dto.CozeUploadImageReponse;
import com.feishu.blog.dto.CozeWorkflowResponseDTO;
import com.feishu.blog.dto.ClassificationDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.feishu.blog.service.LocalImageService.fileToMultipartFile;


@Service
@Slf4j
public class CozeImageService {

    @Value("${coze.api.img-access-token}")
    private String accessToken;

    @Value("${coze.api.img-workflow-id}")
    private String workflowId;

    @Value("${coze.api.img-upload-url}")
    private String uploadUrl;

    @Value("${coze.api.img-workflow-url}")
    private String workflowUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();


    public ClassificationDTO processImageComplianceWithPath(String path) throws IOException {
        MultipartFile file = fileToMultipartFile(path);
        return processImageCompliance(file);
    }

    public ClassificationDTO processImageCompliance(MultipartFile file) throws IOException {
        // Step 1: 上传图片并获取 imageId
        String imageId = uploadImage(file);

        // Step 2: 调用工作流并获取 data
        String workflowData = callWorkflow(imageId);

        // Step 3: 解析工作流返回的 data
        return parseWorkflowData(workflowData);
    }

    private String uploadImage(MultipartFile file) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("Authorization", "Bearer " + accessToken);

        // 使用 MultiValueMap 构造 multipart 请求
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename(); // 保留原始文件名
            }
        });

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<CozeUploadImageReponse> response = restTemplate.exchange(
                uploadUrl, HttpMethod.POST, requestEntity, CozeUploadImageReponse.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null && response.getBody().getCode() == 0) {
            return response.getBody().getData().getId();
        } else {
            throw new RuntimeException("图片上传失败: " + (response.getBody() != null ? response.getBody().getMsg() : "未知错误"));
        }
    }

    private String callWorkflow(String imageId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + accessToken);

        // 构造JSON对象，而不是字符串
        Map<String, Object> input = new HashMap<>();
        input.put("file_id", imageId);

        Map<String, Object> body = new HashMap<>();
        body.put("workflow_id", workflowId);
        body.put("parameters", Collections.singletonMap("input", input));

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<CozeWorkflowResponseDTO> response = restTemplate.exchange(
                workflowUrl, HttpMethod.POST, requestEntity, CozeWorkflowResponseDTO.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null && response.getBody().getCode() == 0) {
            return response.getBody().getData();
        } else {
            throw new RuntimeException("工作流调用失败: " + (response.getBody() != null ? response.getBody().getMsg() : "未知错误"));
        }
    }

    private ClassificationDTO parseWorkflowData(String workflowData) throws IOException {
        return objectMapper.readValue(workflowData, ClassificationDTO.class);
    }
}
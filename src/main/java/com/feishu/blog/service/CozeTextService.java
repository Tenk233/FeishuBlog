package com.feishu.blog.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.feishu.blog.dto.ClassificationDTO;
import com.feishu.blog.dto.CozeWorkflowResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class CozeTextService {

    @Value("${coze.api.txt-access-token}")
    private String accessToken;

    @Value("${coze.api.txt-workflow-id}")
    private String workflowId;

    @Value("${coze.api.txt-workflow-url}")
    private String workflowUrl;


    List<String> validCategories = List.of("积极", "中性", "惊奇");
    List<String> invalidCategories = List.of("愤怒","悲伤","恐惧","惊奇","中性");

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private boolean isValidCategory(String category) {
        return validCategories.contains(category);
    }

    private String callWorkflow(String inputText) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("input", inputText);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("workflow_id", workflowId);
        requestBody.put("parameters", parameters);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<CozeWorkflowResponseDTO> response = restTemplate.postForEntity(
                workflowUrl, entity, CozeWorkflowResponseDTO.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null && response.getBody().getCode() == 0) {
            return response.getBody().getData();
        } else {
            throw new RuntimeException("工作流调用失败: " + (response.getBody() != null ? response.getBody().getMsg() : "未知错误"));
        }
    }

    private ClassificationDTO parseWorkflowData(String workflowData) {
        ClassificationDTO response = new ClassificationDTO();
        try {
            Map<String, String> responseDataMap = objectMapper.readValue(workflowData, new TypeReference<>() {});
            String category = responseDataMap.get("output");
            response.setMsg("Success");
            response.setData(new ClassificationDTO.Data(isValidCategory(category),category));
        } catch (Exception e) {
            response.setCode(1);
            response.setMsg(e.getMessage());
            response.setData(new ClassificationDTO.Data(false,"未知"));
        }
        return response;
    }

    public ClassificationDTO processContentCompliance(String inputText) {
        String workflowData = callWorkflow(inputText);
        return parseWorkflowData(workflowData);
    }
}

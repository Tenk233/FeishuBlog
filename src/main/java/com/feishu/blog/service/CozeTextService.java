package com.feishu.blog.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.feishu.blog.dto.ClassificationDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

//    private final CozeProperties cozeProperties;

    List<String> validCategories = List.of("积极", "中性", "惊奇");
    List<String> invalidCategories = List.of("愤怒","悲伤","恐惧","惊奇","中性");

//    @Autowired
//    public CozeSentimentService(CozeProperties cozeProperties) {
//        this.cozeProperties = cozeProperties;
//    }

    public boolean isValidCategory(String category) {
        return validCategories.contains(category);
    }


    public ClassificationDTO analyzeSentiment(String inputText) {
        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper objectMapper = new ObjectMapper();

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("input", inputText);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("workflow_id", workflowId);
        requestBody.put("parameters", parameters);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        // TODO: Should use CozeWorkflowResponseDTO
        ResponseEntity<String> response = restTemplate.postForEntity(
                workflowUrl, entity, String.class
        );

        ClassificationDTO result = new ClassificationDTO();
        ClassificationDTO.Data data = new ClassificationDTO.Data();

        try {
            Map<String, Object> responseBodyMap = objectMapper.readValue(response.getBody(), new TypeReference<>() {});

            int code = (int) responseBodyMap.get("code");

            result.setCode(code);
            result.setMsg("Fail:" + responseBodyMap.get("msg"));

            if (code != 0) {
                data.setValid(false);
                data.setCategory("未知");
                result.setData(data);
                return result;
            }

            String dataString = (String) responseBodyMap.get("data");
            Map<String, Object> responseDataMap = objectMapper.readValue(dataString, new TypeReference<>() {});
            String category = (String) responseDataMap.get("output");

            data.setValid(isValidCategory(category));
            data.setCategory(category);
            result.setData(data);

        } catch (Exception e) {
            result.setCode(1);
            result.setMsg("Fail:"+ e.getMessage());

            data.setValid(false);
            data.setCategory("未知");
            result.setData(data);
        }

        return result;
    }
}

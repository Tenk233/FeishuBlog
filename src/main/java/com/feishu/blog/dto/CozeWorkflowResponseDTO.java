package com.feishu.blog.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CozeWorkflowResponseDTO {
    private int code;
    private String cost;
    private String data;
    @JsonProperty("debug_url")
    private String debugUrl;
    private String msg;
    private int token;
}

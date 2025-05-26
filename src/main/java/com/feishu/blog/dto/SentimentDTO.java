package com.feishu.blog.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class SentimentDTO {
    private int code;
    private String msg;
    private Data data;

    @lombok.Data
    @AllArgsConstructor
    public static class Data {
        @JsonProperty("is_valid")
        private boolean isValid;
        private String category;
    }
}

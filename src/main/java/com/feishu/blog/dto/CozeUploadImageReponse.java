package com.feishu.blog.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CozeUploadImageReponse {
    private int code;
    private UploadData data;
    private String msg;

    @Data
    public static class UploadData {
        private long bytes;
        @JsonProperty("created_at")
        private long createdAt;
        @JsonProperty("file_name")
        private String fileName;
        private String id;
    }
}
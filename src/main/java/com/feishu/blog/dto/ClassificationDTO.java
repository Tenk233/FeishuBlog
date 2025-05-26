package com.feishu.blog.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ClassificationDTO {

    private int code;
    private String msg;
    private Data data;

    public ClassificationDTO(int code, String msg, Data data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }


    @lombok.Data
    @NoArgsConstructor
    public static class Data {
        @JsonProperty("is_valid")
        private boolean isValid;
        private String category;


        public Data(boolean isValid, String category) {
            this.isValid = isValid;
            this.category = category;
        }

    }
}
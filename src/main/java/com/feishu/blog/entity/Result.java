package com.feishu.blog.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一响应包装
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)   // data 为空时不序列化
public class Result<T> {

    /** 业务状态码：0=成功；1=失败；其他可做扩展 */
    private Integer code;
    /** 提示信息 */
    private String  msg;
    /** 真实返回数据 */
    private T       data;

    /* ---------- 静态工厂 ---------- */

    public static final int SUCCESS = 200;
    public static final int CLIENT_ERROR = 400;
    public static final int UNAUTHENTICATED = 401;
    public static final int NOT_FOUND = 404;
    public static final int SERVER_ERROR = 500;

    public static <T> Result<T> success() {
        return new Result<>(SUCCESS, "success", null);
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(SUCCESS, "success", data);
    }

    public static <T> Result<T> error(String message) {
        return new Result<>(SERVER_ERROR, message, null);
    }

    public static <T> Result<T> error(int code, String message) {
        return new Result<>(code, message, null);
    }
}

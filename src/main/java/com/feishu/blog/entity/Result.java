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

    private static final int SUCCESS = 200;
    private static final int CLIENT_ERROR = 400;
    /* jwt失效 */
    private static final int UNAUTHENTICATED = 401;
    /* 多次登陆失败，需要滑块验证码 */
    private static final int NEED_CAPTCHA = 402;
    /* 资源找不到，例如用户或者博客 */
    private static final int NOT_FOUND = 404;
    private static final int SERVER_ERROR = 500;

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

    public static <T> Result<T> errorClientOperation(String message) {
        return new Result<>(CLIENT_ERROR, message, null);
    }

    public static <T> Result<T> errorUnauthenticated(String message) {
        return new Result<>(UNAUTHENTICATED, message, null);
    }

    public static <T> Result<T> errorToMuchLoginAttempts(T object) {
        return new Result<>(NEED_CAPTCHA, "错误登录次数太多", object);
    }

    public static <T> Result<T> errorResourceNotFound(String message) {
        return new Result<>(NOT_FOUND, message, null);
    }

    public static <T> Result<T> errorServerInternal(String message) {
        return new Result<>(SERVER_ERROR, message, null);
    }
}

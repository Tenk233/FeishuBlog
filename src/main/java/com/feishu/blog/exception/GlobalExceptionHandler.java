package com.feishu.blog.exception;

import com.feishu.blog.entity.Result;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.net.BindException;
import java.util.stream.Collectors;

/**
 * description:
 *
 * @author Tenk
 * @date 2025/4/25
 */

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 捕获所有异常
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<?>> handleGlobalException(Exception ex) {
        ex.printStackTrace();
        Result<?> body = Result.errorServerInternal("Internal Server Error: " + ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    // 捕获资源未找到异常
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Result<?>> handleResourceNotFound(ResourceNotFoundException ex) {
        Result<?> body = Result.errorResourceNotFound("Resource Not Found: " + ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    // 404：没有任何 Handler 匹配
    @ExceptionHandler(NoHandlerFoundException.class)
    public Result<?> handle404(NoHandlerFoundException ex) {
        return Result.errorResourceNotFound("Path Not Match: " + ex.getRequestURL());
    }

    /* ---------- ① JSON 请求体 (@RequestBody) 校验失败 ---------- */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleBodyValidation(MethodArgumentNotValidException ex) {
        return buildValidationResult(ex.getBindingResult());
    }

    /* ---------- ② 表单 / Query Bean (@ModelAttribute) 校验失败 ---------- */
    @ExceptionHandler(BindException.class)
    public Result<?> handleFormValidation(BindException ex) {
        // 在 Spring 6.x 里 BindException 自身就是 BindingResult
        return buildValidationResult((BindingResult) ex);
    }

    /* ---------- ③ 单个参数 / 路径变量 / 方法级校验失败 ---------- */
    @ExceptionHandler(ConstraintViolationException.class)
    public Result<?> handleParamValidation(ConstraintViolationException ex) {
        String msg = ex.getConstraintViolations()
                .stream()
                .map(cv -> cv.getPropertyPath() + " " + cv.getMessage())
                .collect(Collectors.joining("; "));
        return Result.error(400, "字段验证失败: " + msg);
    }

    /* ---------- 公共封装：把 BindingResult → Result ---------- */
    private Result<?> buildValidationResult(BindingResult br) {
        String msg = br.getFieldErrors()
                .stream()
                .map(err -> err.getField() + " " + err.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return Result.error(400, "字段验证失败: " + msg);
    }

}

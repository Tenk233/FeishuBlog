package com.feishu.blog.exception;

import lombok.Getter;

/**
 * description:
 *
 * @author Tenk
 * @date 2025/4/25
 */

@Getter
public class ResourceNotFoundException extends RuntimeException {
    // Getter方法
    // 可以添加更多字段来提供详细的错误信息
    private final String resourceName;
    private final String fieldName;
    private final Object fieldValue;

    // 构造方法
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s : '%s'", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

}

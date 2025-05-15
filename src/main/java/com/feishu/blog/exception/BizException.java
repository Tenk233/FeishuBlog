package com.feishu.blog.exception;

import lombok.Getter;

/**
 * Business Exception
 **/
@Getter
public class BizException extends RuntimeException {

    public static final int REGISTER_EXISTED_USER = 5001;

    public static final int USER_NOT_EXIST = 40004;

    private final int code;

    public BizException(int code, String msg) {
        super(String.format("{code:%s, msg:%s}", code, msg));
        this.code = code;
    }
}
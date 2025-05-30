package com.feishu.blog.exception;

import lombok.Getter;

/**
 * Business Exception
 **/
@Getter
public class BizException extends RuntimeException {

    public static final int INTERNAL_ERROR = 5000;
    public static final int REGISTER_EXISTED_USER = 5001;

    public static final int USER_WRONG_INPUT = 4000;
    public static final int USER_WRONG_PASSWD = 4001;
    public static final int USER_NOT_EXIST = 4004;

    private final int code;

    public BizException(int code, String msg) {
        super(String.format("{code:%s, msg:%s}", code, msg));
        this.code = code;
    }
}
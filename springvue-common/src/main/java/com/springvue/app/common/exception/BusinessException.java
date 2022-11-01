package com.springvue.app.common.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BusinessException extends RuntimeException {

    /**
     * 构建业务异常
     * @param message 错误描述
     */
    public BusinessException(String message) {
        super(message);
    }
}

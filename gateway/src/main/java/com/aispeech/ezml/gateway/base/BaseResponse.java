package com.aispeech.ezml.gateway.base;

import lombok.Data;

/**
 * 基础API响应VO类
 *
 *
 */
@Data
public class BaseResponse<T> {

    private static final String DEFAULT_MESSAGE = "";

    private Integer errorCode;

    private T data;

    private String message;


    public void success() {

        this.errorCode = ErrorCode.DEFAULT.getValue();
    }

    public void success(String message) {
        success();
        this.message = message;
    }

    public void success(String message, T data) {
        success(message);
        this.data = data;
    }

    public void fail() {
        this.errorCode = ErrorCode.ERROR.getValue();
    }

    public void fail(String message) {
        fail();
        this.message = message;
    }

    public void fail(String message, ErrorCode errorCode) {
        fail(message);
        if (null != errorCode) {
            this.errorCode = errorCode.getValue();
        }
    }

}

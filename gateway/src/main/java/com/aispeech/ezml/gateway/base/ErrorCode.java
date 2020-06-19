package com.aispeech.ezml.gateway.base;

public enum ErrorCode {
    SUCCESS(0),
    ERROR(1),
    UNAUTHORIZED(401);

    private int code;

    // 构造方法
    private ErrorCode(int code) {

        this.code = code;
    }

    public int getValue() {
        return code;
    }

    public static ErrorCode getCode(int code) {
        for (ErrorCode c : ErrorCode.values()) {
            if (c.code == code) {
                return c;
            }
        }
        return null;
    }
}

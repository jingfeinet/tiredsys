package com.sicau.tiredsys.common;

public enum ResponseCode {
    SUCCESS(200,"请求成功"),
    ServerError(500,"服务器异常"),
    Unauthorized(401,"身份未被验证"),
    BadRequest(400,"请求错误"),
    Forbidden(403,"请求被禁止"),
    NotFound(404,"资源不存在"),
    ERROR(-1,"错误"),
    UN_KOWNERROR(0,"未知错误"),
    Password_Verification(001,"密码验证"),
    FingerPrint_Verification(002,"指纹验证");

    private final int code;
    private final  String msg;

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
    ResponseCode(int code, String desc) {
        this.code = code;
        this.msg = desc;
        
    }
}

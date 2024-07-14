package com.huaer.resource.admin.enums;

public enum StatusEnum {
    SUCCESS(200, "请求处理成功"),
    UNAUTHORIZED(401,"用户认证失败"),
    FORBIDDEN(403,"权限不足"),
    SERVICE_ERROR(500,"服务器错误，请稍后重试"),
    PARAMS_INVALID(400, "参数无效"),
    BAD_REQUEST(400, "请求错误"),
    ;
    public final Integer code;

    public final String message;

    StatusEnum(Integer code, String message){
        this.code = code;
        this.message = message;
    }

}


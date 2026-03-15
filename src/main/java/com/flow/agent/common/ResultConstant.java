package com.flow.agent.common;


import lombok.Getter;
import lombok.Setter;


@Getter
public enum ResultConstant {
    SUCCESS(20000, "成功"),
    FAIL(20001, "失败"),

    FAIL_FILE_UPLOAD(20010, "文件上传失败"),

    FAIL_NO_LOGIN_ERROR(20003, "用户未登录"),

    FAIL_NO_POWER(20004, "用户权限不足");

    private Integer code;
    private String message;

    private ResultConstant(Integer code, String message){
        this.code = code;
        this.message = message;
    }

}

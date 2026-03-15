package com.flow.agent.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 统一的返回结果类，不管什么类型的数据都可以用它进行封装
// 封装的数据是泛型
@Data // set get 方法
@NoArgsConstructor // 无参构造
@AllArgsConstructor // 有参构造
public class Result <T> {
    private Integer code;
    private String message;
    private T data;



    public static <T> Result<T> success(){
        // 通过枚举进行引用常量
        return new Result<>(ResultConstant.SUCCESS.getCode(), ResultConstant.SUCCESS.getMessage(),null);
    }

    //    方法重载
    public static <T> Result<T> success(String message){
        return new Result<>(20000,message,null);
    }

    public static <T> Result<T> success(T data){
        return new Result<>(ResultConstant.SUCCESS.getCode(), ResultConstant.SUCCESS.getMessage(),data);
    }

    public static <T> Result<T> success(String message, T data){
        return new Result<>(ResultConstant.SUCCESS.getCode(), message,data);
    }

    public static <T> Result<T> fail(){
        return new Result<>(ResultConstant.FAIL.getCode(), ResultConstant.FAIL.getMessage(), null);
    }
    public static <T> Result<T> fail(int code){
        return new Result<>(code, ResultConstant.FAIL.getMessage(),null);
    }
    public static <T> Result<T> fail(String message){

        return new Result<>(20001,message,null);
    }
    public static <T> Result<T> fail(int code, String message){

        return new Result<>(code,message,null);
    }
}

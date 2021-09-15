package com.wot.base;

import java.io.Serializable;

/**
 * 返回结果
 */
public class Result<T> extends ResultError implements Serializable {

    private final T data;

    public Result(T data) {
        super();
        this.data = data;
    }

    public static <T> Result<T> result(T data) {
        return new Result<>(data);
    }

//    public static <T> Result<T> failed(String msg) {
//
//    }


//    public static <T> Result<T> of(String code, T data) {
//        return new Result<>(code, data);
//    }


//    public static <T> Result<T> succeed(String msg) {
//        return of(null, CodeEnum.SUCCESS.getCode(), msg);
//    }
//
//    public static <T> Result<T> succeed(T model, String msg) {
//        return of(model, CodeEnum.SUCCESS.getCode(), msg);
//    }
//
//    public static <T> Result<T> succeed(T model) {
//        return of(model, CodeEnum.SUCCESS.getCode(), "");
//    }
//
//    public static <T> Result<T> failed(String msg) {
//        return of(null, CodeEnum.ERROR.getCode(), msg);
//    }
//
//    public static <T> Result<T> failed(T model, String msg) {
//        return of(model, CodeEnum.ERROR.getCode(), msg);
//    }
}

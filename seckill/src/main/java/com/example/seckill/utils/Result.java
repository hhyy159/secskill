package com.example.seckill.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//2026.5.30 余浩洋 首次创建于2026.5.30 统一返回结果类
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {
    private int code;
    private String message;
    private T data;
    public Result(T data) {
        this.code = 200;
        this.message = "success";
        this.data = data;
    }
    public Result(T data, boolean success, String message) {
        if (success) {
            this.code = 200;
            this.message = "success";
        } else {
            this.code = 500;
            this.message = message;
        }
        this.data = data;
    }
    public Result(int code, String message) {
        this.code = code;
        this.message = message;
        this.data = null;
    }
    public static <T> Result<T> success(T data) {
        return new Result<>(data);
    }
    public static <T> Result<T> fail(String message) {
        return new Result<>(500, message);
    }
    public static <T> Result<T> fail(int code, String message) {
        return new Result<>(code, message);
    }
}


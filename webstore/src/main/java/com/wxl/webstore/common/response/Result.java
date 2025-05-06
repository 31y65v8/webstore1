package com.wxl.webstore.common.response;

import lombok.Data;

@Data
public class Result<T> {
    private int code;    // 状态码
    private String msg;  // 消息
    private T data;      // 返回数据

    // 成功响应（无数据）
    public static <T> Result<T> success() {
        return success(null);
    }

    // 成功响应（带数据）
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMsg("success");
        result.setData(data);
        return result;
    }

    // 错误响应
    public static <T> Result<T> error(int code, String msg) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }

    // 错误响应（单个String参数）
    public static <T> Result<T> error(String message) {
        Result<T> result = new Result<>();
        result.setCode(500); // 默认使用500错误码
        result.setMsg(message);
        return result;
    }
}
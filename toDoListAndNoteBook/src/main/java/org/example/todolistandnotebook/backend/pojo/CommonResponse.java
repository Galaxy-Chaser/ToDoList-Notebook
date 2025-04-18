package org.example.todolistandnotebook.backend.pojo;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class CommonResponse<T> {
    private Integer code;
    private String message;
    private T data;

    // 构造函数
    public CommonResponse(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // 静态方法：成功响应（带数据）
    public static <T> CommonResponse<T> success(T data) {
        return new CommonResponse<>(HttpStatus.OK.value(), "success", data);
    }

    // 静态方法：成功响应（无数据）
    public static <T> CommonResponse<T> success() {
        return new CommonResponse<>(HttpStatus.OK.value(), "success", null);
    }

    // 静态方法：找不到资源（无数据）
    public static <T> CommonResponse<T> notFound() {
        return new CommonResponse<>(HttpStatus.NOT_FOUND.value(), "not found", null);
    }

    // 静态方法：错误（无数据）
    public static <T> CommonResponse<T> error() {
        return new CommonResponse<>(HttpStatus.BAD_REQUEST.value(), "error", null);
    }

    // 静态方法：错误（有数据）
    public static <T> CommonResponse<T> error(String errorMessage) {
        return new CommonResponse<>(HttpStatus.BAD_REQUEST.value(), errorMessage, null);
    }
}

package com.gushu.gushumingmu.User.Util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultUtil<T> {
    // 状态码常量定义
    public static final int SUCCESS_CODE = 200;
    public static final int ERROR_CODE = 400;
    public static final int FORBIDDEN_CODE = 403;
    public static final int UNAUTHORIZED_CODE = 401;
    public static final int NOT_FOUND_CODE = 404;
    public static final int SERVER_ERROR_CODE = 500;

    private int code;  // 状态码
    private String msg;  // 提示信息
    private T data;  // 响应数据

    // 成功响应（带数据）
    public static <T> ResultUtil<T> success(T data) {
        return new ResultUtil<>(SUCCESS_CODE, "操作成功", data);
    }

    // 成功响应（带自定义消息）
    public static <T> ResultUtil<T> success(String msg, T data) {
        return new ResultUtil<>(SUCCESS_CODE, msg, data);
    }

    // 成功响应（仅消息）
    public static ResultUtil<Void> success(String msg) {
        return new ResultUtil<>(SUCCESS_CODE, msg, null);
    }

    // 成功响应（无数据无消息）
    public static ResultUtil<Void> success() {
        return new ResultUtil<>(SUCCESS_CODE, "操作成功", null);
    }

    // -------------- 新增泛型错误方法（核心修复）--------------
    // 错误响应（自定义消息+泛型支持）
    public static <T> ResultUtil<T> errorWithType(String msg) {
        return new ResultUtil<>(ERROR_CODE, msg, null);
    }

    // 权限不足（403 + 泛型支持）
    public static <T> ResultUtil<T> forbiddenWithType(String msg) {
        return new ResultUtil<>(FORBIDDEN_CODE, msg, null);
    }

    // 未授权（401 + 泛型支持）
    public static <T> ResultUtil<T> unauthorizedWithType(String msg) {
        return new ResultUtil<>(UNAUTHORIZED_CODE, msg, null);
    }
    // ---------------------------------------------------------

    // 原有错误方法（保持不变，用于不需要泛型的场景）
    public static ResultUtil<Void> error(String msg) {
        return new ResultUtil<>(ERROR_CODE, msg, null);
    }

    public static ResultUtil<Void> forbidden() {
        return new ResultUtil<>(FORBIDDEN_CODE, "权限不足，无法访问", null);
    }

    public static ResultUtil<Void> forbidden(String msg) {
        return new ResultUtil<>(FORBIDDEN_CODE, msg, null);
    }

    public static ResultUtil<Void> unauthorized() {
        return new ResultUtil<>(UNAUTHORIZED_CODE, "未授权，请重新登录", null);
    }

    public static ResultUtil<Void> unauthorized(String msg) {
        return new ResultUtil<>(UNAUTHORIZED_CODE, msg, null);
    }

    public static ResultUtil<Void> notFound() {
        return new ResultUtil<>(NOT_FOUND_CODE, "请求的资源不存在", null);
    }

    public static ResultUtil<Void> notFound(String msg) {
        return new ResultUtil<>(NOT_FOUND_CODE, msg, null);
    }

    public static ResultUtil<Void> serverError() {
        return new ResultUtil<>(SERVER_ERROR_CODE, "服务器内部错误，请稍后重试", null);
    }

    public static ResultUtil<Void> serverError(String msg) {
        return new ResultUtil<>(SERVER_ERROR_CODE, msg, null);
    }
}
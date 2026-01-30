package com.gushu.gushumingmu.User.Exception;


import com.gushu.gushumingmu.User.Util.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 处理业务异常
    @ExceptionHandler(BusinessException.class)
    public ResultUtil<Void> handleBusinessException(BusinessException e) {
        log.error("业务异常: {}", e.getMessage());
        return ResultUtil.error(e.getMessage());
    }

    // 处理用户不存在异常
    @ExceptionHandler(UserNotFoundException.class)
    public ResultUtil<Void> handleUserNotFoundException(UserNotFoundException e) {
        log.error("用户不存在: {}", e.getMessage());
        return ResultUtil.error(e.getMessage());
    }

    // 处理权限不足异常
    @ExceptionHandler(PermissionDeniedException.class)
    public ResultUtil<Void> handlePermissionDeniedException(PermissionDeniedException e) {
        log.error("权限不足: {}", e.getMessage());
        return ResultUtil.forbidden();
    }

    // 处理Spring Security权限不足（如@PreAuthorize校验失败）
    @ExceptionHandler(AccessDeniedException.class)
    public ResultUtil<Void> handleAccessDeniedException(AccessDeniedException e) {
        log.error("访问被拒绝: {}", e.getMessage());
        return ResultUtil.forbidden();
    }

    // 处理登录密码错误
    @ExceptionHandler(BadCredentialsException.class)
    public ResultUtil<Void> handleBadCredentialsException(BadCredentialsException e) {
        log.error("认证失败: {}", e.getMessage());
        return ResultUtil.error("用户名或密码错误");
    }
    /**
     * 处理参数校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResultUtil<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        StringBuilder errorMsg = new StringBuilder();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            errorMsg.append(fieldError.getField()).append(":").append(fieldError.getDefaultMessage()).append(";");
        }
        log.error("参数校验异常: {}", errorMsg.toString());
        return ResultUtil.error(errorMsg.toString());
    }
    // 处理其他异常
    @ExceptionHandler(Exception.class)
    public ResultUtil<Void> handleException(Exception e) {
        log.error("系统异常: ", e);
        return ResultUtil.error("系统异常，请联系管理员");
    }
}
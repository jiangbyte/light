package io.jiangbyte.github.config;

import io.jiangbyte.github.types.enums.ResponseCode;
import io.jiangbyte.github.types.exception.BizException;
import io.jiangbyte.github.types.response.Response;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.stream.Collectors;

/**
 * 全局异常处理：统一转为 Response JSON，避免 Spring Boot 默认错误页。
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常。
     */
    @ExceptionHandler(BizException.class)
    public ResponseEntity<Response<Void>> handleBizException(BizException ex) {
        // 1. 将业务异常中的状态码数值映射为 HttpStatus
        HttpStatus status = HttpStatus.resolve(ex.getHttpStatus());
        if (status == null) {
            status = HttpStatus.BAD_REQUEST;
        }
        // 2. 按业务码组装统一响应
        return build(status, Response.fail(ex.getCode(), ex.getMessage()));
    }

    /**
     * 处理无匹配 Handler（404）。
     */
    @ExceptionHandler({NoHandlerFoundException.class, NoResourceFoundException.class})
    public ResponseEntity<Response<Void>> handleNotFound(Exception ex, HttpServletRequest request) {
        // 1. 记录访问路径，便于排查错误入口
        log.warn("资源不存在: {} {}", request.getMethod(), request.getRequestURI());
        // 2. 返回统一 404 JSON
        return build(HttpStatus.NOT_FOUND, Response.fail(ResponseCode.NOT_FOUND));
    }

    /**
     * 处理不支持的 HTTP 方法（405）。
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Response<Void>> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex) {
        return build(HttpStatus.METHOD_NOT_ALLOWED, Response.fail(ResponseCode.METHOD_NOT_ALLOWED, ex.getMessage()));
    }

    /**
     * 处理请求体校验失败。
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Response<Void>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        // 1. 汇总字段错误文案
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(this::formatFieldError)
                .collect(Collectors.joining("; "));
        if (!StringUtils.hasText(message)) {
            message = ResponseCode.ILLEGAL_PARAMETER.getInfo();
        }
        // 2. 按非法参数码返回
        return build(HttpStatus.BAD_REQUEST, Response.fail(ResponseCode.ILLEGAL_PARAMETER, message));
    }

    /**
     * 处理表单/查询参数绑定失败。
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<Response<Void>> handleBindException(BindException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(this::formatFieldError)
                .collect(Collectors.joining("; "));
        if (!StringUtils.hasText(message)) {
            message = ResponseCode.ILLEGAL_PARAMETER.getInfo();
        }
        return build(HttpStatus.BAD_REQUEST, Response.fail(ResponseCode.ILLEGAL_PARAMETER, message));
    }

    /**
     * 处理缺少必填请求参数。
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Response<Void>> handleMissingParameter(MissingServletRequestParameterException ex) {
        String message = "缺少参数: " + ex.getParameterName();
        return build(HttpStatus.BAD_REQUEST, Response.fail(ResponseCode.ILLEGAL_PARAMETER, message));
    }

    /**
     * 兜底未知异常，避免堆栈与 HTML 错误页泄漏。
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response<Void>> handleException(Exception ex, HttpServletRequest request) {
        // 1. 记录完整异常，对外只给统一失败码
        log.error("未处理异常: {} {}", request.getMethod(), request.getRequestURI(), ex);
        // 2. 返回通用失败响应
        return build(HttpStatus.INTERNAL_SERVER_ERROR, Response.fail(ResponseCode.UN_ERROR));
    }

    /**
     * 组装带 HTTP 状态的统一响应；强制 JSON，避免按 Accept 回退到 HTML 错误页。
     */
    private ResponseEntity<Response<Void>> build(HttpStatus status, Response<Void> body) {
        return ResponseEntity.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }

    /**
     * 将字段校验错误格式化为可读文案。
     */
    private String formatFieldError(FieldError fieldError) {
        String defaultMessage = fieldError.getDefaultMessage();
        if (StringUtils.hasText(defaultMessage)) {
            return fieldError.getField() + " " + defaultMessage;
        }
        return fieldError.getField() + " 非法";
    }

}

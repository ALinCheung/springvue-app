package com.springvue.app.common.component;

import com.springvue.app.common.exception.BusinessException;
import com.springvue.app.common.model.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.OK)
    public Result businessException(HttpServletRequest request, HttpServletResponse response, Exception e, HandlerMethod method) {
        log.info(getMethodSchema(method) + "失败, 原因:{}", e.getMessage());
        return Result.fail(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)
    public Result exception(HttpServletRequest request, HttpServletResponse response, Exception e, HandlerMethod method) {
        log.error(getMethodSchema(method) + "异常, 原因:{}", e.getMessage(), e);
        return Result.fail(e.getMessage());
    }

    /**
     * 获取控制器方法上的swagger注解/方法名
     * @param method
     * @return
     */
    private String getMethodSchema(HandlerMethod method) {
        StringBuilder msg = new StringBuilder();
        // 获取类名
        Api api = AnnotationUtils.findAnnotation(method.getBeanType(), Api.class);
        if (api != null) {
            if (StringUtils.isNotBlank(api.value())) {
                msg.append(api.value());
            } else if (api.tags().length > 0) {
                msg.append(api.tags()[0]);
            } else {
                msg.append(method.getBeanType().getName());
            }
        } else {
            msg.append(method.getBeanType().getName());
        }
        msg.append("-");
        // 获取方法名
        ApiOperation apiOperation = AnnotationUtils.findAnnotation(method.getMethod(), ApiOperation.class);
        if (apiOperation != null) {
            if (StringUtils.isNotBlank(apiOperation.value())) {
                msg.append(apiOperation.value());
            } else if (apiOperation.tags().length > 0) {
                msg.append(apiOperation.tags()[0]);
            } else {
                msg.append(method.getMethod().getName());
            }
        } else {
            msg.append(method.getMethod().getName());
        }
        return msg.toString();
    }
}

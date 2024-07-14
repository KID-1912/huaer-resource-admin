package com.huaer.resource.admin.exception;

import com.huaer.resource.admin.dto.ResultResponse;
import com.huaer.resource.admin.enums.StatusEnum;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.UnexpectedTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {
    final Logger logger = LoggerFactory.getLogger(getClass());

    // 处理ServiceException
    @ExceptionHandler(ServiceException.class)
    @ResponseBody
    public ResultResponse<Void> handleServiceException(ServiceException serviceException, HttpServletRequest request) {
        // todo:日志记录
        logger.warn("request {} throw ServiceException \n", request, serviceException);
        return ResultResponse.error(serviceException.getStatus(), serviceException.getMessage());
    }

    // 其他异常拦截
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResultResponse<Void> handleException(Exception ex, HttpServletRequest request) {
        // todo:日志记录
        logger.error("request {} throw unExpectException \n", request, ex);
        return ResultResponse.error(StatusEnum.SERVICE_ERROR);
    }

    // 处理单个请求参数验证或自定义验证的错误
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    public ResultResponse<Void> handlerConstraintViolationException(ConstraintViolationException ex) {
        // 自定义错误消息
        String errorMessage = ex.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        return ResultResponse.error(StatusEnum.PARAMS_INVALID, errorMessage);
    }

    // 处理验证@RequestBody错误
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResultResponse<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex){
        try{
            List<ObjectError> errors = ex.getBindingResult().getAllErrors();
            String message = errors.stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining(","));
            logger.error("param illegal: {}", message);
            return ResultResponse.error(StatusEnum.PARAMS_INVALID, message);
        }catch(Exception e){
            return ResultResponse.error(StatusEnum.SERVICE_ERROR);
        }
    }

    // 处理解析参数时参数缺失错误
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseBody
    public ResultResponse<Void> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex, HttpServletRequest request){
        logger.error("request {} throw MissingServletRequestParameterException \n", request, ex);
        return ResultResponse.error(StatusEnum.PARAMS_INVALID);
    }

    // 处理不符合预期的数据类型异常: Spring 表单绑定或数据绑定时,以及参数解析
    @ExceptionHandler({UnexpectedTypeException.class,MethodArgumentTypeMismatchException.class})
    @ResponseBody
    public ResultResponse<Void> handleUnexpectedTypeException(Exception ex){
        logger.error("catch UnexpectedTypeException, errorMessage:\n", ex);
        return ResultResponse.error(StatusEnum.PARAMS_INVALID, ex.getMessage());
    }

    // 处理请求方法不支持异常
    @ExceptionHandler({HttpRequestMethodNotSupportedException.class, HttpMediaTypeException.class})
    @ResponseBody
    public ResultResponse<Void> handleMethodNotSupportedException(Exception ex){
        logger.error("HttpRequestMethodNotSupportedException \n", ex);
        return ResultResponse.error(StatusEnum.BAD_REQUEST);
    }

    // 处理无法读取HTTP消息的异常，请求体不合法或不可解析（如格式错误json）
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public ResultResponse<Void> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex, HttpServletRequest request){
        logger.error("request {} throw ucManagerException \n", request, ex);
        return ResultResponse.error(StatusEnum.BAD_REQUEST);
    }

}


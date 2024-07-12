package com.huaer.resource.admin.exception;

import com.huaer.resource.admin.dto.ResultResponse;
import com.huaer.resource.admin.enums.StatusEnum;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ServiceException.class)
    @ResponseBody
    public ResultResponse<Void> handleServiceException(ServiceException serviceException, HttpServletRequest request) {
        // todo:日志记录
        return ResultResponse.error(serviceException.getStatus(), serviceException.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResultResponse<Void> handleException(Exception ex, HttpServletRequest request) {
        // todo:日志记录
        return ResultResponse.error(StatusEnum.SERVICE_ERROR);
    }
}


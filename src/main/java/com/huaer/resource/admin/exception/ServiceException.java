package com.huaer.resource.admin.exception;

import com.huaer.resource.admin.enums.StatusEnum;
import lombok.Getter;

import java.io.Serial;

@Getter
public class ServiceException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -3303518302920463234L;

    private final StatusEnum status;

    public ServiceException(StatusEnum status, String message) {
        super(message);
        this.status = status;
    }

    public ServiceException(StatusEnum status){
        this(status, status.message
        );
    }

}

package com.huaer.resource.admin.dto;

import com.huaer.resource.admin.enums.StatusEnum;

import java.io.Serial;
import java.io.Serializable;

public class ResultResponse<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = -1133637474601003587L;

    /*
    接口响应状态码
    */
    private Integer code;

    /*
    接口响应信息
    */
    private String msg;

    /*
    接口响应的数据
    */
    private T data;

    /*
    成功响应方法
    @param data 响应数据
    @return response
    @param <T> 响应数据类型
    */
    public static <T> ResultResponse<T> success(T data){
        ResultResponse<T> response = new ResultResponse<>();
        response.setData(data);
        response.setCode(StatusEnum.SUCCESS.code);
        return response;
    }

    /*
    error响应方法 StatusEnum作为状态信息
     */
    public static <T> ResultResponse<T> error(StatusEnum statusEnum){
        return error(statusEnum, statusEnum.message);
    }

    /*
    error响应方法 可自定义错误信息
    @param statusEnum error响应的状态值
    @return
    @param <T>
    */
    public static <T> ResultResponse<T> error(StatusEnum statusEnum, String errorMsg){
        ResultResponse<T> response = new ResultResponse<>();
        response.setCode(statusEnum.code);
        response.setMsg(errorMsg);
        return response;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}

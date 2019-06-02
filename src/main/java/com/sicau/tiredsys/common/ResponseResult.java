package com.sicau.tiredsys.common;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;

@JsonSerialize
public class ResponseResult<T> implements Serializable {
    private int status;
    private String msg;
    private T data;

    private ResponseResult(int status) {
        this.status = status;
    }

    private ResponseResult(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    private ResponseResult(int status, String msg, T data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    private ResponseResult(int status, T data) {
        this.status = status;
        this.data = data;
    }

    //使之不在json序列化结果中
    @JsonIgnore
    public boolean isSuccess(){
        return this.status== ResponseCode.SUCCESS.getCode();
    }

    public int getStatus(){
        return status;
    }
    public T getData(){
        return data;
    }
    public String getMsg(){
        return msg;
    }

    public static <T> ResponseResult<T> createBySuccess(){
        return new ResponseResult<T>(ResponseCode.SUCCESS.getCode());
    }

    public static <T> ResponseResult<T> createBySuccess(T data){
        return new ResponseResult<T>(ResponseCode.SUCCESS.getCode(),data);
    }

    public static <T> ResponseResult<T> createBySuccess(String msg, T data){
        return new ResponseResult<T>(ResponseCode.SUCCESS.getCode()
                ,msg,data);
    }

    public static <T> ResponseResult<T> createBySuccessMessage(String msg){
        return new ResponseResult<T>(ResponseCode.SUCCESS.getCode(),msg);
    }

    public static <T> ResponseResult<T> createByError(){
        return new ResponseResult<T>(ResponseCode.ERROR.getCode(),
                ResponseCode.ERROR.getMsg());
    }

    public static <T> ResponseResult<T> createByErrorMessage(String errorMessage){
        return new ResponseResult<T>(ResponseCode.ERROR.getCode(),
                errorMessage);
    }

    public static <T> ResponseResult<T> createByErrorCodeMessage(int errorCode, String errorMessage){
        return new ResponseResult<T>(errorCode,errorMessage);
    }

    public static <T> ResponseResult<T> createByErrorData(ResponseCode responseCode,T data){
        return new ResponseResult<T>(responseCode.getCode(),responseCode.getMsg(),data);
    }





}

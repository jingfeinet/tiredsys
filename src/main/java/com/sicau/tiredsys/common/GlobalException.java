package com.sicau.tiredsys.common;

import com.sicau.tiredsys.common.ResponseCode;
import com.sicau.tiredsys.common.ResponseResult;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Created by zhong  on 2019/3/14 14:09
 */
//全局异常捕获
@RestControllerAdvice
@EnableConfigurationProperties({ServerProperties.class})
public class GlobalException {

    @ExceptionHandler(NullPointerException.class)
    public ResponseResult nullPointerException(NullPointerException ex){
        return ResponseResult.createByErrorCodeMessage(ResponseCode.ERROR.getCode(),"空指针异常,可能没有登录");
    }

    @ExceptionHandler(AuthenticationException.class)
    public  ResponseResult authenticationException(AuthenticationException ex){
        return ResponseResult.createByErrorCodeMessage(ResponseCode.Unauthorized.getCode(),ex.getMessage());
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseResult handleShiroException(UnauthorizedException ex) {
        return ResponseResult.createByErrorCodeMessage(ResponseCode.Forbidden.getCode(),ResponseCode.Forbidden.getMsg());
    }

    @ExceptionHandler(Exception.class)
    public ResponseResult exception(Exception ex){
        return ResponseResult.createByErrorMessage(ResponseCode.ServerError.getMsg());
    }


    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseResult httpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex){
        return ResponseResult.createByErrorCodeMessage(ResponseCode.Forbidden.getCode(),ResponseCode.Forbidden.getMsg());
    }

    @ExceptionHandler(UnauthenticatedException.class)
    public ResponseResult unauthenticatedException(UnauthenticatedException ex){
        return ResponseResult.createByErrorCodeMessage(ResponseCode.Unauthorized.getCode(),ex.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseResult HttpMessageNotReadableException(HttpMessageNotReadableException ex){
        return ResponseResult.createByErrorMessage(ex.getMessage());
    }

}

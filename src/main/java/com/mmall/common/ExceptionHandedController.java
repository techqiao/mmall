package com.mmall.common;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <p>Description : book_shop
 * <p>Date : 2018-04-12 10:51
 * <p>@author : wjq
 */
@RestControllerAdvice//返回json格式给前端
public class ExceptionHandedController {
    //处理UserException异常
    @ExceptionHandler(UserException.class)
    public Result handedException(UserException e) {
        return Result.error(e.getMessage());
    }
}

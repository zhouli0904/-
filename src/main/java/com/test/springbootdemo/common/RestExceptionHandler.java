package com.test.springbootdemo.common;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// 文档https://www.cnblogs.com/gemiaomiao/p/11900564.html
@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler({BizException.class})
    public Result<?> bizExceptionHandler(BizException e) {
        return Result.ofFail(e.getCode(), e.getMsg());
    }

    @ExceptionHandler({Throwable.class})
    public Result<?> exceptionHandler(Throwable e) {
        return Result.ofFail(500, e.getMessage());
    }
}

package com.test.springbootdemo.common;

import lombok.Data;

@Data
public class BizException extends RuntimeException {

    private String msg;

    private Integer code;

    public BizException(Integer code, String message) {
        this.code = code;
        this.msg = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }





}

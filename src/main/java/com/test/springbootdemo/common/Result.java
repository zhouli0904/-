package com.test.springbootdemo.common;

import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

@Data
public class Result<T> implements Serializable {

    private static final long serialVersionUID = -2819439914286233874L;
    public static final Long SUCCESS_CODE = 200L;
    public static final Long FAIL_CODE = 400L;
    public static final String DEFAULT_SUCCESS_MESSAGE = "success";
    private long code;
    private String msg;
    private T data;

    public Result() {
        this.setCode(SUCCESS_CODE);
        this.setMsg(DEFAULT_SUCCESS_MESSAGE);
    }


    public Result(long code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public Result(String msg) {
        this.setCode(FAIL_CODE);
        this.msg = msg;
    }

    public Result(T data) {
        this.setCode(SUCCESS_CODE);
        this.setMsg(DEFAULT_SUCCESS_MESSAGE);
        this.data = data;
    }

    public static <T> Result<T> ofSuccess(T data) {
        return new Result<>(data);
    }

    public static Result ofFail(String msg) {
        return new Result(msg);
    }
    public static Result<?> ofFail(long code, String msg) {
        return new Result<>(code, msg, null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Result<?> result = (Result<?>) o;
        return code == result.code && msg.equals(result.msg) && data.equals(result.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, msg, data);
    }
}

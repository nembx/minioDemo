package com.nembx.mypan.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Lian
 */

@Data
public class BaseResponse<T> implements Serializable {
    private int code;

    private T data;

    private String message;

    public BaseResponse(int code, T data, String message){
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public BaseResponse(int code, T data){
        this.code = code;
        this.data = data;
    }

    public BaseResponse(int code,String message){
        this.code = code;
        this.message = message;
    }

    public BaseResponse(ErrorCode errorCode){
        this(errorCode.getCode(), null, errorCode.getMessage());
    }
}

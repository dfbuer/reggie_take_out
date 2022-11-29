package com.buer.regi.common;

/**
 * 拦截异常
 */
public class CustomException extends RuntimeException{

    public CustomException(String message){
        super(message);
    }
}

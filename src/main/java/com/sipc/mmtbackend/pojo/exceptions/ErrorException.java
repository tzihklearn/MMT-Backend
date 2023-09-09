package com.sipc.mmtbackend.pojo.exceptions;

@SkipNotice
public class ErrorException extends RuntimeException{
    public ErrorException(String message) {
        super(message);
    }
}

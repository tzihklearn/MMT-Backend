package com.sipc.mmtbackend.pojo.exceptions;

/**
 * 自定义的运行异常
 * @author tzih
 * @version v1.0
 * @since 2023.04.23
 */
public class RunException extends Exception{
    public RunException() {
        super();
    }

    public RunException(String message) {
        super(message);
    }
}

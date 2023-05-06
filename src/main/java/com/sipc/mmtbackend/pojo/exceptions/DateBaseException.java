package com.sipc.mmtbackend.pojo.exceptions;

/**
 * 自定义的数据库操作异常
 * @author tzih
 * @version v1.0
 * @since 2023.04.23
 */
public class DateBaseException extends Exception{
    public DateBaseException() {
        super();
    }

    public DateBaseException(String message) {
        super(message);
    }
}

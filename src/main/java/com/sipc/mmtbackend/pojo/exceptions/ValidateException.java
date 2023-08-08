package com.sipc.mmtbackend.pojo.exceptions;

/**
 * @author tzih
 * @version v1.0
 * @since 2023.08.08
 */
public class ValidateException extends Exception {
    public ValidateException() {
        super();
    }

    public ValidateException(String message) {
        super(message);
    }
}

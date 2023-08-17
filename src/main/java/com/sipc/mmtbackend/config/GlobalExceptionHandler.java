package com.sipc.mmtbackend.config;

import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.pojo.exceptions.DateBaseException;
import com.sipc.mmtbackend.pojo.exceptions.RunException;
import com.sipc.mmtbackend.pojo.exceptions.ValidateException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author tzih
 * @version v1.0
 * @since 2023.05.18
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({DateBaseException.class, ValidateException.class})
    public CommonResult<String> DateAndValExceptionHandler(Exception e) {
        e.printStackTrace();

        return CommonResult.serverError();
    }

    @ExceptionHandler({RunException.class})
    public CommonResult<String> RunExceptionHandler(RunException e) {
        e.printStackTrace();
        if (e.getIsReturn()) {
            return CommonResult.fail(e.getMessage());
        } else {
            return CommonResult.serverError();
        }
    }

}

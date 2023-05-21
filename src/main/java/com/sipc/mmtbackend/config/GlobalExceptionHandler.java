package com.sipc.mmtbackend.config;

import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.pojo.exceptions.DateBaseException;
import com.sipc.mmtbackend.pojo.exceptions.RunException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author tzih
 * @version v1.0
 * @since 2023.05.18
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({DateBaseException.class, RunException.class})
    public CommonResult<String> DateAndRunExceptionHandler(Exception e) {
        e.printStackTrace();

        return CommonResult.fail("请求错误");
    }

}

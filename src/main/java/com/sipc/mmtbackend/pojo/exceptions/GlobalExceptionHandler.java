package com.sipc.mmtbackend.pojo.exceptions;


import com.sipc.mmtbackend.pojo.dto.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public CommonResult MethodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        String message = null;
        if (bindingResult.hasErrors()) {
            FieldError fieldError = bindingResult.getFieldError();
            if (fieldError != null) {
                message = fieldError.getField() + fieldError.getDefaultMessage();
            }
        }
        return CommonResult.fail(message);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public CommonResult MissingServletRequestParameterExceptionHandler(MissingServletRequestParameterException e) {

        return CommonResult.fail(e.getParameterName() + "不能为空");
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public CommonResult HttpRequestMethodNotSupportedExceptionHandler(HttpRequestMethodNotSupportedException e) {
        return CommonResult.fail("Http Request Method Not Supported");
    }


    @ExceptionHandler(ErrorException.class)
    public CommonResult ErrorExceptionHandler(ErrorException e) {
        return CommonResult.fail("10002", e.getMessage());
    }



//    @ExceptionHandler(DataIntegrityViolationException.class)
//    public CommonResult<NoData> DataIntegrityViolationExceptionHandler(DataIntegrityViolationException e) {
//        return CommonResult.fail("请求参数异常！请检查各字段长度是否合适或合规");
//    }
}

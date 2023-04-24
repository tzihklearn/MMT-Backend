package com.sipc.mmtbackend.pojo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sipc.mmtbackend.pojo.dto.resultEnum.ResultEnum;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommonResult<T> {

    /**
     * 状态码
     */
    private String code;
    /**
     * 提醒
     */
    private String message;
    /**
     * 返回的数据
     */
    private T data;

    public CommonResult(){}

    public CommonResult(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 成功返回结果
     * @param
     */
    public static <T> CommonResult<T> success() {
        return new CommonResult<>(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMessage(), null);
    }


    /**
     * 成功返回结果
     *
     * @param message 获取的信息
     */
    public static <T> CommonResult<T> success(String message) {
        return new CommonResult<>(ResultEnum.SUCCESS.getCode(), message, null);
    }

    /**
     * 成功返回结果
     *
     * @param data 获取的数据
     */
    public static <T> CommonResult<T> success(T data) {
        return new CommonResult<T>(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMessage(), data);
    }

    /**
     * 失败返回结果
     */
    public static <T> CommonResult<T> fail() {
        return new CommonResult<T>(ResultEnum.FAILED.getCode(), ResultEnum.FAILED.getMessage(), null);
    }

    /**
     * 失败返回结果
     *
     * @param message 错误信息
     */
    public static <T> CommonResult<T> fail(String message) {
        return new CommonResult<T>(ResultEnum.FAILED.getCode(), message, null);
    }

    /**
     * 失败返回结果
     */
    public static <T> CommonResult<T> fail_login() {
        return new CommonResult<T>(ResultEnum.USER_Logged_EXIST.getCode(), ResultEnum.USER_Logged_EXIST.getMessage(), null);
    }

    /**
     * 失败返回结果
     *
     * @param code    错误码
     * @param message 错误信息
     * @param <T>     返回对象封装类
     * @return 返回请求对象
     */
    public static <T> CommonResult<T> fail(String code, String message) {
        return new CommonResult<T>(code, message, null);
    }

    /**
     * 操作失败返回结果
     */
    public static <T> CommonResult<T> userResourceException() {
        return new CommonResult<T>(ResultEnum.USER_RESOURCE_EXCEPTION.getCode(),
                ResultEnum.USER_RESOURCE_EXCEPTION.getMessage(), null);
    }

    /**
     * 操作失败返回结果
     *
     * @param data 获取的数据
     */
    public static <T> CommonResult<T> userResourceException(T data) {
        return new CommonResult<T>(ResultEnum.USER_RESOURCE_EXCEPTION.getCode(),
                ResultEnum.USER_RESOURCE_EXCEPTION.getMessage(), data);
    }

    /**
     * 操作失败返回结果
     *
     * @param message 错误信息
     */
    public static <T> CommonResult<T> userResourceException(String message) {
        return new CommonResult<T>(ResultEnum.USER_RESOURCE_EXCEPTION.getCode(), message, null);
    }

    /**
     * 用户登录过期返回结果
     */
    public static <T> CommonResult<T> userLoginExpired() {
        return new CommonResult<T>(ResultEnum.USER_LOGIN_EXPIRED.getCode(),
                ResultEnum.USER_LOGIN_EXPIRED.getMessage(), null);
    }

    /**
     * 用户登录过期返回结果
     */
    public static <T> CommonResult<T> userPasswordWrong() {
        return new CommonResult<T>(ResultEnum.USER_PASSWORD_WRONG.getCode(),
                ResultEnum.USER_PASSWORD_WRONG.getMessage(), null);
    }

    /**
     * 用户名已存在返回结果
     */
    public static <T> CommonResult<T> userUsernameExist() {
        return new CommonResult<T>(ResultEnum.USER_USERNAME_EXIST.getCode(),
                ResultEnum.USER_USERNAME_EXIST.getMessage(), null);
    }

    /**
     * 用户权限错误
     */
    public static <T> CommonResult<T> userAuthError() {
        return new CommonResult<T>(ResultEnum.AUTH_ERROR.getCode(), ResultEnum.AUTH_ERROR.getMessage(), null);
    }

    /**
     * 服务器执行错误
     */
    public static <T> CommonResult<T> serverError() {
        return new CommonResult<T>(ResultEnum.SERVER_ERROR.getCode(), ResultEnum.SERVER_ERROR.getMessage(), null);
    }

    /**
     * 用户身份验证错误
     */
    public static <T> CommonResult<T> loginError() {
        return new CommonResult<T>(ResultEnum.TOKEN_ERROR.getCode(), ResultEnum.TOKEN_ERROR.getMessage(), null);
    }
}

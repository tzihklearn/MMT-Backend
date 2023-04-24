package com.sipc.mmtbackend.pojo.dto.resultEnum;
import lombok.Getter;

/**
 * API操作码
 */
@Getter
public enum ResultEnum {
    SUCCESS("00000", "请求正常"),
    FAILED("A0400", "请求失败"),
    USER_RESOURCE_EXCEPTION("A0600", "用户资源异常"),
    USER_LOGIN_EXPIRED("A0230", "用户登录已过期"),
    USER_USERNAME_EXIST("A0111", "用户名已存在"),
    USER_PASSWORD_WRONG("A0210", "用户密码错误"),
    WECHAT_SERVER_ERROR("C0001", "微信服务接口错误"),
    TOKEN_ERROR("A0220", "用户身份验证错误"),
    SERVER_ERROR("B0001", "服务器执行错误"),
    AUTH_ERROR("A0300", "访问权限异常"),
    USER_Logged_EXIST("A0200", "用户已在另一台设备登录");

    private String code;
    private String message;

    private ResultEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }
}

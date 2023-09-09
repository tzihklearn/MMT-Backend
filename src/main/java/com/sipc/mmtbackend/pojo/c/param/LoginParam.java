package com.sipc.mmtbackend.pojo.c.param;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * @author 徐嘉豪
 * b端登录账号密码参数封装
 */

@Data
public class LoginParam {
    @NotBlank
    @Pattern(regexp = "^201\\d{5}|202\\d{5}$", message = "学号不符合规范")
    public String studentId;

    @NotBlank
    @Pattern(regexp = "^.{6,16}$", message = "密码不符合规范")
    public String password;
}

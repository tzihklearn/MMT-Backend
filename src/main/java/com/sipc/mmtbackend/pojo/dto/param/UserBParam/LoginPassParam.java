package com.sipc.mmtbackend.pojo.dto.param.UserBParam;

import lombok.Data;

/**
 * B端使用账号密码登录的请求体
 */
@Data
public class LoginPassParam {
    private String studentId;
    private Integer organizationId;
    private String password;
}

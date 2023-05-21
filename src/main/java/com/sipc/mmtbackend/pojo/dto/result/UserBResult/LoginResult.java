package com.sipc.mmtbackend.pojo.dto.result.UserBResult;

import lombok.Data;

@Data
public class LoginResult {
    private Integer userId;
    private String username;
    private String token;
    private Integer permissionId;
    private String permissionName;
}

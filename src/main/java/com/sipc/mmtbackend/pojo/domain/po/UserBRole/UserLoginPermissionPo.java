package com.sipc.mmtbackend.pojo.domain.po.UserBRole;

import lombok.Data;

@Data
public class UserLoginPermissionPo {
    private Integer userId;
    private String password;
    private String username;
    private Integer permissionId;
    private String permissionName;
    private Integer organizationId;
}

package com.sipc.mmtbackend.pojo.domain.po.UserBRole;

import lombok.Data;

@Data
public class UserRolePermissionPo {
    private Integer organizationId;
    private Integer permissionId;
    private String permissionName;
}

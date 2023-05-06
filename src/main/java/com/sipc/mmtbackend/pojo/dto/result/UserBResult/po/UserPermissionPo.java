package com.sipc.mmtbackend.pojo.dto.result.UserBResult.po;

import lombok.Data;

@Data
public class UserPermissionPo {
    private Integer organizationId;
    private Integer permissionId;
    private String permissionName;
}

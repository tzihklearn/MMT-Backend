package com.sipc.mmtbackend.utils.CheckroleBUtil.pojo;

import lombok.Data;

@Data
public class CheckRoleResult {
    private Integer userId;
    private String username;
    private String studentId;
    private Integer organizationId;
    private String organizationName;
    private Integer permissionId;
    private String permissionName;
}

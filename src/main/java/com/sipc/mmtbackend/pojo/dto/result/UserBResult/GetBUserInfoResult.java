package com.sipc.mmtbackend.pojo.dto.result.UserBResult;

import lombok.Data;

@Data
public class GetBUserInfoResult {
    private Integer userId;
    private String username;
    private String phone;
    private String studentId;
    private Integer organizationId;
    private String organizationName;
    private Integer permissionId;
    private String permissionName;
}

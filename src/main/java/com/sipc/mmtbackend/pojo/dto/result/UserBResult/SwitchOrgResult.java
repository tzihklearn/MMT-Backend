package com.sipc.mmtbackend.pojo.dto.result.UserBResult;

import lombok.Data;

@Data
public class SwitchOrgResult {
    private Integer permissionId;
    private String permissionName;
    private String token;
}

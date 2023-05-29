package com.sipc.mmtbackend.pojo.dto.result.UserBResult.po;

import lombok.Data;

@Data
public class LoginedJoinedOrgResultPo {
    private Integer organizationId;
    private String organizationName;
    private Boolean active;
}

package com.sipc.mmtbackend.utils.CheckroleBUtil.pojo;

import com.sipc.mmtbackend.pojo.domain.po.UserBRole.UserLoginPermissionPo;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class BTokenSwapPo {
    private Integer userId;
    private String token;
    private Integer organizationId;
    private Integer permissionId;

//    @Deprecated
    public BTokenSwapPo(UserLoginPermissionPo po) {
        this.userId = po.getUserId();
        this.organizationId = po.getOrganizationId();
        this.permissionId = po.getPermissionId();
    }
}

package com.sipc.mmtbackend.utils.CheckroleBUtil.pojo;

import com.sipc.mmtbackend.pojo.domain.po.UserBRole.UserLoginPermissionPo;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class BTokenSwapPo {
    private Integer userId;
    private Integer roleId;
    private String studentId;
    private Integer organizationId;
    private Integer permissionId;

    @Deprecated
    public BTokenSwapPo(UserLoginPermissionPo po) {
        this.userId = po.getUserId();
        this.studentId = po.getStudentId();
        this.organizationId = po.getOrganizationId();
        this.permissionId = po.getPermissionId();
        this.roleId = po.getRoleId();
    }
}

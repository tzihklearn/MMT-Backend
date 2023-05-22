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
    public BTokenSwapPo(UserLoginPermissionPo po){
        this.userId = po.getUserId();
        this.studentId = po.getStudentId();
        this.roleId = po.getRoleId();
    }
}

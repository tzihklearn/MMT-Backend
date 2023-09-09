package com.sipc.mmtbackend.pojo.c.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class UserDepartmentRegistration {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    private Integer admissionId;
    private Integer departmentId;
    private Integer organizationOrder;
    private Integer departmentOrder;
}

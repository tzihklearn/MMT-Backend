package com.sipc.mmtbackend.pojo.c.domain;

import lombok.Data;

@Data
public class UserDepartmentRegistration {
    private Integer id;
    private Integer userId;
    private Integer admissionId;
    private Integer departmentId;
    private Integer organizationOrder;
    private Integer departmentOrder;
}

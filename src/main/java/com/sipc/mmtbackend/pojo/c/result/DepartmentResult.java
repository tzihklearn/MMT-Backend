package com.sipc.mmtbackend.pojo.c.result;

import lombok.Data;

import java.io.Serializable;

@Data
public class DepartmentResult implements Serializable {
    private Integer departmentId;
    private String departmentName;
}

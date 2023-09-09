package com.sipc.mmtbackend.pojo.c.result.ShowOrganization;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class DepartmentAdmissionResult implements Serializable {
    private Integer departmentId;
    private String name;
    private String introduction;

    public DepartmentAdmissionResult() {
    }
}

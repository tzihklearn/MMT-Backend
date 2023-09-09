package com.sipc.mmtbackend.pojo.c.param.ShowOrganizationData;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class OrganizationOrDepartmentIntroduceData implements Serializable{

    private String titleName;
    private String content;

    public OrganizationOrDepartmentIntroduceData() {
    }
}

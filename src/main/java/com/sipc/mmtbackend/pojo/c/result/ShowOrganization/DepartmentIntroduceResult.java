package com.sipc.mmtbackend.pojo.c.result.ShowOrganization;

import com.sipc.mmtbackend.pojo.c.param.ShowOrganizationData.OrganizationOrDepartmentIntroduceData;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class DepartmentIntroduceResult implements Serializable {
    private String avatarUrl;
    private String name;
    private String registrationTime;
    private List<OrganizationOrDepartmentIntroduceData> DepartmentIntroduceDataList;

    public DepartmentIntroduceResult() {
    }
}

package com.sipc.mmtbackend.pojo.c.result.ShowOrganization;

import com.sipc.mmtbackend.pojo.c.param.ShowOrganizationData.OrganizationOrDepartmentIntroduceData;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class OrganizationIntroduceResult implements Serializable{

    private String title;
    private String avatarUrl;
    private String name;
    private String startTime;
    private String endTime;
    private String registrationTime;
    private List<OrganizationOrDepartmentIntroduceData> organizationIntroduceDataList;

    private Integer allowDepartmentAmount;

    public OrganizationIntroduceResult(){}


}


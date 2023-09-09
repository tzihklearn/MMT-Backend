package com.sipc.mmtbackend.pojo.c.param.organizationListData;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class OrganizationListData {

    private Integer admissionId;
    private Integer organizationId;
    private String avatarUrl;
    private String name;
    private List<String> tag;
    private String registrationTime;
    private String tagStatus;
    private String description;

    public OrganizationListData() {
    }
}

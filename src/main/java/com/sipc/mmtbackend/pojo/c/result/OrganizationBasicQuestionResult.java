package com.sipc.mmtbackend.pojo.c.result;

import lombok.Data;

import java.util.List;

@Data
public class OrganizationBasicQuestionResult {
    private Integer admissionId;
    private Integer allowReallocation;
    private Integer allowDepartmentAmount;
    private List<DepartmentResult> organizationAllowDepartment;
    private UserBasicInformation userBasicInformation;
    private List<String> generalQuestions;
}

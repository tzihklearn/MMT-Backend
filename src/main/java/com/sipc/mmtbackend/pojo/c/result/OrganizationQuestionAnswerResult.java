package com.sipc.mmtbackend.pojo.c.result;

import lombok.Data;

import java.util.List;

@Data
public class OrganizationQuestionAnswerResult {
    private String status;
    private List<OrganizationQuestionAnswerData> organizationQuestionAnswerList;
}

package com.sipc.mmtbackend.pojo.c.result;

import lombok.Data;

@Data
public class OrganizationQuestionAnswerData {
    private Integer questionId;

    private String type;

    private Integer questionOrder;

    private String description;

    private Boolean selection;

    private String answer;
}

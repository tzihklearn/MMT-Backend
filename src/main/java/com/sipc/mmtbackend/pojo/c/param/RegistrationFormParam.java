package com.sipc.mmtbackend.pojo.c.param;

import lombok.Data;

import java.util.List;

@Data
public class RegistrationFormParam {

    private Integer admissionId;

    private Integer organizationOrder;

    private Integer allowReallocation;

    private EssentialInformationData essentialInformation;

    private UserSignData userSign;

    private List<AnswerData> questionAnswerList;
}

package com.sipc.mmtbackend.pojo.c.param;

import lombok.Data;

import java.util.List;

/**
 * @author tzih
 * @version v1.0
 * @since 2023.09.10
 */
@Data
public class RegistrationFormParamPo {

//    private Integer admissionId;
//
//    private Integer organizationOrder;
//
//    private Integer allowReallocation;
//
//    private EssentialInformationData essentialInformation;
//
//    private UserSignData userSign;
//
//    private List<AnswerData> questionAnswerList;
//
//    private Integer userId;
//
//    public RegistrationFormParamPo(RegistrationFormParam registrationFormParam, Integer userId) {
//        this.admissionId = registrationFormParam.getAdmissionId();
//        this.organizationOrder = registrationFormParam.getOrganizationOrder();
//        this.allowReallocation = registrationFormParam.getAllowReallocation();
//        this.essentialInformation = registrationFormParam.getEssentialInformation();
//        this.userSign = registrationFormParam.getUserSign();
//        this.questionAnswerList = registrationFormParam.getQuestionAnswerList();
//        this.userId = userId;
//    }

    private RegistrationFormParam registrationFormParam;

    private Integer userId;

    public RegistrationFormParamPo(RegistrationFormParam registrationFormParam, Integer userId) {
        this.registrationFormParam = registrationFormParam;
        this.userId = userId;
    }

    public RegistrationFormParamPo() {

    }

}

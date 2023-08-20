package com.sipc.mmtbackend.service;

import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.pojo.dto.data.QuestionPoData;
import com.sipc.mmtbackend.pojo.dto.param.superAdmin.*;
import com.sipc.mmtbackend.pojo.dto.result.superAdmin.*;
import com.sipc.mmtbackend.pojo.dto.result.superAdmin.po.SelectTypePo;
import com.sipc.mmtbackend.pojo.exceptions.DateBaseException;
import com.sipc.mmtbackend.pojo.exceptions.RunException;

import java.util.List;

public interface OrganizationService {

    CommonResult<String> updateOrganizationInfo(OrganizationInfoParam organizationInfoParam) throws DateBaseException, RunException;

    CommonResult<OrganizationInfoResult> getOrganizationInfo();

    CommonResult<UploadAvatarResult> uploadAvatar() throws DateBaseException;

    CommonResult<String> publishAdmission(AdmissionPublishParam admissionPublishParam) throws DateBaseException, RunException;

    CommonResult<String> saveRegistrationForm(RegistrationFormParam registrationFormParam) throws DateBaseException, RunException;

    CommonResult<RegistrationFormResult> getRegistrationForm() throws RunException;

    CommonResult<List<QuestionPoData>> getSystemQuestion() throws RunException;

    CommonResult<List<SelectTypePo>> getSelectType();

    CommonResult<String> saveInterviewFrom(InterviewFormParam interviewFormParam) throws RunException, DateBaseException;

    CommonResult<InterviewFromResult> getInterviewFrom() throws RunException;

    CommonResult<MessageTemplateResult> setMessageTemplate(MessageTemplateParam messageTemplateParam) throws DateBaseException;

    CommonResult<MessageTemplateResult> getMessageTemplate();
}

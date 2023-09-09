package com.sipc.mmtbackend.service.c;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sipc.mmtbackend.pojo.c.param.RegistrationFormParam;
import com.sipc.mmtbackend.pojo.c.result.NoData;
import com.sipc.mmtbackend.pojo.c.result.OrganizationBasicQuestionResult;
import com.sipc.mmtbackend.pojo.c.result.OrganizationQuestionAnswerResult;
import com.sipc.mmtbackend.pojo.c.result.OrganizationQuestionResult;
import com.sipc.mmtbackend.pojo.dto.CommonResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author yuleng
 * @Date 2022/8/11
 * @Version 3.0
 */
public interface COrganizationInterviewService {


    CommonResult<RegistrationFormParam> setRegistrationForm(RegistrationFormParam registrationFormParam,
                                                            HttpServletRequest httpServletRequest,
                                                            HttpServletResponse httpServletResponse);

    CommonResult<OrganizationQuestionResult> getOrganizationQuestion(Integer admissionID,
                                                                     HttpServletRequest httpServletRequest,
                                                                     HttpServletResponse httpServletResponse) throws JsonProcessingException;

    CommonResult<OrganizationBasicQuestionResult> getAdmissionBasicSign(Integer admissionID,
                                                                        HttpServletRequest httpServletRequest,
                                                                        HttpServletResponse httpServletResponse);

    CommonResult<OrganizationQuestionAnswerResult> getOrganizationQuestionAnswer(Integer admissionID,
                                                                                 HttpServletRequest httpServletRequest,
                                                                                 HttpServletResponse httpServletResponse);

    CommonResult<NoData> interviewMessageC(Integer admissionId, HttpServletRequest request,
                                           HttpServletResponse response);

    CommonResult<Boolean> check(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Integer admissionId);
}

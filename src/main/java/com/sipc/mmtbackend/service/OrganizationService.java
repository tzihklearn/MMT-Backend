package com.sipc.mmtbackend.service;

import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.pojo.dto.param.superAdmin.OrganizationInfoParam;
import com.sipc.mmtbackend.pojo.dto.param.superAdmin.AdmissionPublishParam;
import com.sipc.mmtbackend.pojo.dto.param.superAdmin.RegistrationFormParam;
import com.sipc.mmtbackend.pojo.dto.result.superAdmin.OrganizationInfoResult;
import com.sipc.mmtbackend.pojo.dto.result.superAdmin.RegistrationFormResult;
import com.sipc.mmtbackend.pojo.dto.result.superAdmin.UploadAvatarResult;
import com.sipc.mmtbackend.pojo.exceptions.DateBaseException;
import com.sipc.mmtbackend.pojo.exceptions.RunException;

public interface OrganizationService {

    CommonResult<String> updateOrganizationInfo(OrganizationInfoParam organizationInfoParam) throws DateBaseException, RunException;

    CommonResult<OrganizationInfoResult> getOrganizationInfo();

    CommonResult<UploadAvatarResult> uploadAvatar() throws DateBaseException;

    CommonResult<String> publishAdmission(AdmissionPublishParam admissionPublishParam) throws DateBaseException, RunException;

    CommonResult<String> saveRegistrationForm(RegistrationFormParam registrationFormParam) throws DateBaseException, RunException;

    CommonResult<RegistrationFormResult> getRegistrationForm() throws RunException;
}

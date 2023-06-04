package com.sipc.mmtbackend.service;

import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.pojo.dto.param.superAdmin.OrganizationInfoParam;
import com.sipc.mmtbackend.pojo.dto.param.superAdmin.OrganizationPublishParam;
import com.sipc.mmtbackend.pojo.dto.result.superAdmin.OrganizationInfoResult;
import com.sipc.mmtbackend.pojo.dto.result.superAdmin.UploadAvatarResult;
import com.sipc.mmtbackend.pojo.exceptions.DateBaseException;
import com.sipc.mmtbackend.pojo.exceptions.RunException;

public interface OrganizationService {

    CommonResult<String> updateOrganizationInfo(OrganizationInfoParam organizationInfoParam) throws DateBaseException, RunException;

    CommonResult<OrganizationInfoResult> getOrganizationInfo();

    CommonResult<UploadAvatarResult> uploadAvatar() throws DateBaseException;

    CommonResult<String> publishAdmission(OrganizationPublishParam organizationPublishParam) throws DateBaseException, RunException;

}

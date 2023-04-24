package com.sipc.mmtbackend.service;

import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.pojo.dto.param.OrganizationInfoParam;
import com.sipc.mmtbackend.pojo.exceptions.DateBaseException;
import com.sipc.mmtbackend.pojo.exceptions.RunException;

public interface OrganizationInfoService {

    CommonResult<String> updateOrganizationInfo(OrganizationInfoParam organizationInfoParam) throws DateBaseException, RunException;

}

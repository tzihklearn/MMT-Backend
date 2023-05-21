package com.sipc.mmtbackend.service;

import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.pojo.dto.result.superAdmin.ICodeResult;

public interface AccountManageService {

    CommonResult<ICodeResult> generatedICode(Integer organizationId);

}

package com.sipc.mmtbackend.service;

import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.pojo.dto.result.superAdmin.ICodeResult;

public interface SuperAminService {

    CommonResult<ICodeResult> generatedICode(Integer organizationId);

}

package com.sipc.mmtbackend.service;

import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.pojo.dto.result.ICodeResult;

public interface SuperAminService {

    public CommonResult<ICodeResult> generatedICode(Integer organizationId);

}

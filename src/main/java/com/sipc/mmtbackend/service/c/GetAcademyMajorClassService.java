package com.sipc.mmtbackend.service.c;

import com.sipc.mmtbackend.pojo.c.result.GetAcademyResult;
import com.sipc.mmtbackend.pojo.c.result.GetClassNumResult;
import com.sipc.mmtbackend.pojo.c.result.GetMajorResult;
import com.sipc.mmtbackend.pojo.dto.CommonResult;

public interface GetAcademyMajorClassService {
    CommonResult<GetAcademyResult> getAcademy();

    CommonResult<GetMajorResult> getMajor(Integer academyId);

    CommonResult<GetClassNumResult> getClassNum(Integer academyId, Integer majorId);
}

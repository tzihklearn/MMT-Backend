package com.sipc.mmtbackend.service.c;


import com.sipc.mmtbackend.pojo.c.result.ShowOrganization.DepartmentAdmissionResult;
import com.sipc.mmtbackend.pojo.c.result.ShowOrganization.DepartmentIntroduceResult;
import com.sipc.mmtbackend.pojo.c.result.ShowOrganization.OrganizationIntroduceResult;
import com.sipc.mmtbackend.pojo.dto.CommonResult;

import java.util.List;

public interface ShowOrganizationService {

    CommonResult<OrganizationIntroduceResult> getOrganizationIntroduce(Integer admissionId, Integer organizationId);

    CommonResult<List<DepartmentAdmissionResult>> getDepartmentAdmission(Integer admissionId, Integer organizationId);

    CommonResult<DepartmentIntroduceResult> getDepartmentIntroduce(Integer admissionId, Integer organizationId,
                                                                   Integer departmentId);

}

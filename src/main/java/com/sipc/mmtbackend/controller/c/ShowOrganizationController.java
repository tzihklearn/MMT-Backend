package com.sipc.mmtbackend.controller.c;

import com.sipc.mmtbackend.pojo.c.result.ShowOrganization.DepartmentAdmissionResult;
import com.sipc.mmtbackend.pojo.c.result.ShowOrganization.DepartmentIntroduceResult;
import com.sipc.mmtbackend.pojo.c.result.ShowOrganization.OrganizationIntroduceResult;
import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.service.c.ShowOrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 汤子涵
 */

@RestController
@RequestMapping(value = "/c/show-organizations", method = RequestMethod.GET)
public class ShowOrganizationController {

    private final ShowOrganizationService showOrganizationService;

    @Autowired
    public ShowOrganizationController(ShowOrganizationService showOrganizationService) {
        this.showOrganizationService = showOrganizationService;
    }

    @GetMapping("/organization-introduce")
    public CommonResult<OrganizationIntroduceResult> getOrganizationIntroduce(@RequestParam("admissionId") Integer admissionId,
                                                                              @RequestParam("organizationId") Integer organizationId) {
        return showOrganizationService.getOrganizationIntroduce(admissionId, organizationId);
    }

    @GetMapping("/department-admission")
    public CommonResult<List<DepartmentAdmissionResult>> getDepartmentAdmission(@RequestParam("admissionId") Integer admissionId,
                                                                                @RequestParam("organizationId") Integer organizationId) {
        return showOrganizationService.getDepartmentAdmission(admissionId, organizationId);
    }

    @GetMapping("/department-introduce")
    public CommonResult<DepartmentIntroduceResult> getDepartmentIntroduce(@RequestParam("admissionId") Integer admissionId,
                                                                          @RequestParam("organizationId") Integer organizationId,
                                                                          @RequestParam("departmentId") Integer departmentId) {
        return showOrganizationService.getDepartmentIntroduce(admissionId, organizationId, departmentId);
    }

}

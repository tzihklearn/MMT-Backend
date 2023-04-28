package com.sipc.mmtbackend.controller;

import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.pojo.dto.param.OrganizationInfoParam;
import com.sipc.mmtbackend.pojo.dto.result.OrganizationInfoResult;
import com.sipc.mmtbackend.service.OrganizationInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author tzih
 * @version v1.0
 * @since 2023.04.23
 */
@RestController
@RequestMapping("/organization/info")
@RequiredArgsConstructor(onConstructor = @_(@Autowired))
public class OrganizationInfoController {


    private final OrganizationInfoService organizationInfoService;

    @PostMapping("/update")
    public CommonResult<String> updateOrganizationInfo(@RequestBody OrganizationInfoParam organizationInfoParam) {
        try {
            return organizationInfoService.updateOrganizationInfo(organizationInfoParam);
        }
        catch (Exception e) {
            e.printStackTrace();
            return CommonResult.fail("操作失败");
        }
    }

    @GetMapping("/get")
    public CommonResult<OrganizationInfoResult> getOrganizationInfo(
            @RequestParam("organizationId") Integer organizationId
    ) {
        return organizationInfoService.getOrganizationInfo(organizationId);
    }

}

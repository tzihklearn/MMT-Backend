package com.sipc.mmtbackend.controller;

import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.pojo.dto.param.OrganizationInfoParam;
import com.sipc.mmtbackend.service.OrganizationInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}

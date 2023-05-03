package com.sipc.mmtbackend.controller;

import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.pojo.dto.param.OrganizationInfoParam;
import com.sipc.mmtbackend.pojo.dto.result.OrganizationInfoResult;
import com.sipc.mmtbackend.service.OrganizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 社团宣传与面试相关接口的控制层
 * @author tzih
 * @version v1.0
 * @since 2023.04.23
 */
@RestController
@RequestMapping("/organization")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class OrganizationController {


    private final OrganizationService organizationService;

    /**
     * 设置社团宣传信息的接口的控制层处理，请求方法POST，请求路径/organization/info/update
     * @param organizationInfoParam 社团社团宣传信息的接口的请求体参数的实体类
     * @see com.sipc.mmtbackend.pojo.dto.param.OrganizationInfoParam
     * @return CommonResult<<String>> 返回接口处理的结果
     */
    @PostMapping("/info/update")
    public CommonResult<String> updateOrganizationInfo(@RequestBody OrganizationInfoParam organizationInfoParam) {
        try {
            return organizationService.updateOrganizationInfo(organizationInfoParam);
        }
        catch (Exception e) {
            e.printStackTrace();
            return CommonResult.fail("操作失败");
        }
    }

    /**
     * 获取社团宣传信息的接口，请求方法GET,请求路径/organization/info/get
     * @param organizationId 社团组织id
     * @return CommonResult<<OrganizationInfoResult>> 返回接口处理的结果，含有社团的纳新宣传信息
     * @see com.sipc.mmtbackend.pojo.dto.result.OrganizationInfoResult
     */
    @GetMapping("/info/get")
    public CommonResult<OrganizationInfoResult> getOrganizationInfo(
            @RequestParam("organizationId") Integer organizationId
    ) {
        return organizationService.getOrganizationInfo(organizationId);
    }

}

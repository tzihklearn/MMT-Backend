package com.sipc.mmtbackend.controller.superAdmin;

import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.pojo.dto.param.superAdmin.OrganizationPublishParam;
import com.sipc.mmtbackend.pojo.dto.result.superAdmin.UploadAvatarResult;
import com.sipc.mmtbackend.pojo.dto.param.superAdmin.OrganizationInfoParam;
import com.sipc.mmtbackend.pojo.dto.result.superAdmin.OrganizationInfoResult;
import com.sipc.mmtbackend.pojo.exceptions.DateBaseException;
import com.sipc.mmtbackend.pojo.exceptions.RunException;
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
@RequestMapping("/b/admin/organization")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class OrganizationController {


    private final OrganizationService organizationService;

    /**
     * 设置社团宣传信息的接口的控制层处理，请求方法POST，请求路径/organization/info/update
     * @param organizationInfoParam 社团社团宣传信息的接口的请求体参数的实体类
     * @see OrganizationInfoParam
     * @return CommonResult<<String>> 返回接口处理的结果
     */
    @PostMapping("/info/update")
    public CommonResult<String> updateOrganizationInfo(@RequestBody OrganizationInfoParam organizationInfoParam) throws RunException, DateBaseException {

        return organizationService.updateOrganizationInfo(organizationInfoParam);
    }

    /**
     * 获取社团宣传信息的接口，请求方法GET,请求路径/organization/info/get
     * @return CommonResult<<OrganizationInfoResult>> 返回接口处理的结果，含有社团的纳新宣传信息
     * @see OrganizationInfoResult
     */
    @GetMapping("/info/get")
    public CommonResult<OrganizationInfoResult> getOrganizationInfo() {
        return organizationService.getOrganizationInfo();
    }

    /**
     * 上传社团头像的接口，请求方法Post,请求路径/organization/avatar/upload，接受采用form-data接受图像文件
     * @return CommonResult<UploadAvatarResult> 返回接口处理的结果，含有社团的头像url
     */
    @PostMapping("/avatar/upload")
    public CommonResult<UploadAvatarResult> uploadAvatar() throws DateBaseException {

        return organizationService.uploadAvatar();
    }

    @PostMapping("/admission/publish")
    public CommonResult<String> publishAdmission(@RequestBody OrganizationPublishParam organizationPublishParam) {
        return null;
    }

    @PostMapping("/test")
    public CommonResult<String> test() throws RuntimeException {
        throw new RuntimeException("test测试错误");
//        return null;
//        return null;
    }

}

package com.sipc.mmtbackend.controller.superAdmin;

import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.pojo.dto.data.QuestionPoData;
import com.sipc.mmtbackend.pojo.dto.param.superAdmin.OrganizationInfoParam;
import com.sipc.mmtbackend.pojo.dto.param.superAdmin.AdmissionPublishParam;
import com.sipc.mmtbackend.pojo.dto.param.superAdmin.RegistrationFormParam;
import com.sipc.mmtbackend.pojo.dto.result.superAdmin.OrganizationInfoResult;
import com.sipc.mmtbackend.pojo.dto.result.superAdmin.RegistrationFormResult;
import com.sipc.mmtbackend.pojo.dto.result.superAdmin.UploadAvatarResult;
import com.sipc.mmtbackend.pojo.dto.result.superAdmin.po.SelectTypePo;
import com.sipc.mmtbackend.pojo.exceptions.DateBaseException;
import com.sipc.mmtbackend.pojo.exceptions.RunException;
import com.sipc.mmtbackend.service.OrganizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 社团宣传与面试相关接口的控制层
 *
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
     *
     * @param organizationInfoParam 社团社团宣传信息的接口的请求体参数的实体类
     * @return CommonResult<< String>> 返回接口处理的结果
     * @see OrganizationInfoParam
     */
    @PostMapping("/info/update")
    public CommonResult<String> updateOrganizationInfo(@RequestBody OrganizationInfoParam organizationInfoParam) throws RunException, DateBaseException {

        return organizationService.updateOrganizationInfo(organizationInfoParam);
    }

    /**
     * 获取社团宣传信息的接口，请求方法GET,请求路径/organization/info/get
     *
     * @return CommonResult<< OrganizationInfoResult>> 返回接口处理的结果，含有社团的纳新宣传信息
     * @see OrganizationInfoResult
     */
    @GetMapping("/info/get")
    public CommonResult<OrganizationInfoResult> getOrganizationInfo() {
        return organizationService.getOrganizationInfo();
    }

    /**
     * 上传社团头像的接口，请求方法Post,请求路径/organization/avatar/upload，接受采用form-data接受图像文件
     *
     * @return CommonResult<UploadAvatarResult> 返回接口处理的结果，含有社团的头像url
     */
    @PostMapping("/avatar/upload")
    public CommonResult<UploadAvatarResult> uploadAvatar() throws DateBaseException {

        return organizationService.uploadAvatar();
    }

    /**
     * 提交报名表并发起纳新的接口，请求方法Post,请求路径/b/admin/organization/admission/publish
     * @param admissionPublishParam 提交报名表并发起纳新的接口的请求参数
     * @return CommonResult<String> 返回接口处理结果
     * @throws RunException 自定义的运行时异常，抛出用于统一异常处理
     * @throws DateBaseException 自定义的数据库操作异常，抛出用于统一异常处理
     * @see AdmissionPublishParam
     */
    @PostMapping("/admission/publish")
    public CommonResult<String> publishAdmission(@RequestBody AdmissionPublishParam admissionPublishParam) throws RunException, DateBaseException {
        return organizationService.publishAdmission(admissionPublishParam);
    }

    /**
     * 保存社团报名表信息接口，请求方法Post,请求路径/b/admin/organization/registration/form/save
     * @param registrationFormParam 保存社团报名表信息接口的请求参数
     * @return CommonResult<String> 返回接口处理结果
     * @throws RunException 自定义的运行时异常，抛出用于统一异常处理
     * @throws DateBaseException 自定义的数据库操作异常，抛出用于统一异常处理
     * @see RegistrationFormParam
     */
    @PostMapping("/registration/form/save")
    public CommonResult<String> saveRegistrationForm(@RequestBody RegistrationFormParam registrationFormParam) throws RunException, DateBaseException {
        return organizationService.saveRegistrationForm(registrationFormParam);
    }

    /**
     * 获取社团报名表信息接口，请求方法Get,请求路径/b/admin/organization/registration/form/info
     * @return CommonResult<RegistrationFormResult> 返回社团报名表相关信息的实体类
     * @throws RunException 自定义的运行时异常，抛出用于统一异常处理
     * @see RegistrationFormResult
     */
    @GetMapping("/registration/form/info")
    public CommonResult<RegistrationFormResult> getRegistrationForm() throws RunException {
        return organizationService.getRegistrationForm();
    }

    /**
     * 获取系统内置问题接口，请求方法Get,请求路径/b/admin/organization/registration/form/system/question
     * @return 返回系统内置问题列表
     * @throws RunException 自定义的运行时异常，抛出用于统一异常处理
     * @see QuestionPoData
     */
    @GetMapping("/registration/form/system/question")
    public CommonResult<List<QuestionPoData>> getSystemQuestion() throws RunException {
        return organizationService.getSystemQuestion();
    }

    /**
     * 获取选择类型列表接口，请求方法Get,请求路径/b/admin/organization/registration/form/select/type
     * @return 返回选择类型列表
     * @see SelectTypePo
     */
    @GetMapping("/registration/form/select/type")
    public CommonResult<List<SelectTypePo>> getSelectType() {
        return organizationService.getSelectType();
    }

    @PostMapping("/test")
    public CommonResult<String> test() throws RuntimeException {
        throw new RuntimeException("test测试错误");
//        return null;
//        return null;
    }

}

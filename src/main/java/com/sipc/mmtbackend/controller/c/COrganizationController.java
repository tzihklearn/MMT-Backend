package com.sipc.mmtbackend.controller.c;

import com.sipc.mmtbackend.pojo.c.param.RegistrationFormParam;
import com.sipc.mmtbackend.pojo.c.result.NoData;
import com.sipc.mmtbackend.pojo.c.result.OrganizationBasicQuestionResult;
import com.sipc.mmtbackend.pojo.c.result.OrganizationQuestionAnswerResult;
import com.sipc.mmtbackend.pojo.c.result.OrganizationQuestionResult;
import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.pojo.dto.resultEnum.ResultEnum;
import com.sipc.mmtbackend.service.c.COrganizationInterviewService;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
public class COrganizationController {

    //设置log记录
    Logger log = Logger.getLogger("CommonControllerLog");

    @Resource
    private COrganizationInterviewService COrganizationInterviewService;

    @Resource
    private HttpServletRequest httpServletRequest;

    @Resource
    private HttpServletResponse httpServletResponse;


    /**
     * 报名表提交控制层
     *
     * @param registrationFormParam 社团报名信息体
     * @return 通用返回
     */
    @RequestMapping(value = "/c/organization/sign", method = RequestMethod.PUT)
    public CommonResult<RegistrationFormParam> setRegistrationForm(@RequestBody @Valid RegistrationFormParam registrationFormParam, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, BindingResult result) {
        log.setLevel(Level.INFO);
        String message = CheckData(result);
        if (message != null) return CommonResult.fail(message);
        try {
            return COrganizationInterviewService.setRegistrationForm(registrationFormParam, httpServletRequest,
                    httpServletResponse);
        } catch (Exception e) {
            e.printStackTrace();
            if ("Cannot parse null string".equals(e.getMessage()) || "null".equals(e.getMessage()))
                return CommonResult.loginError();
            return CommonResult.fail();
        }
    }

    /**
     * 判断是否可报名该组织
     */
    @GetMapping("/c/organization/check")
    public CommonResult<Boolean> checkSign(
            @RequestParam(value = "admissionId") Integer admissionId
    ) {
        return COrganizationInterviewService.check(httpServletRequest, httpServletResponse, admissionId);
    }

    /**
     * 获取社团面试问题
     *
     * @param admissionID 纳新id
     * @return 社团面试轮次返回体
     */
    @RequestMapping(value = "/c/organization/question", method = RequestMethod.GET)
    public CommonResult<OrganizationQuestionResult> getOrganizationQuestion(@RequestParam("admissionId") Integer admissionID,
                                                                            HttpServletRequest request,
                                                                            HttpServletResponse response) {
        log.setLevel(Level.INFO);
        //确保是正数
        if (admissionID <= 0) return CommonResult.fail("传入数据格式错误");
        try {
            return COrganizationInterviewService.getOrganizationQuestion(admissionID, request, response);
        } catch (Exception e) {
            e.printStackTrace();
            if ("Cannot parse null string".equals(e.getMessage()) || "null".equals(e.getMessage()))
                return CommonResult.loginError();
            return CommonResult.fail();
        }
    }

    /**
     * 获取纳新基本问题
     *
     * @param admissionID 纳新id
     * @return 社团面试轮次返回体
     */
    @RequestMapping(value = "/c/organization/basic-sign", method = RequestMethod.GET)
    public CommonResult<OrganizationBasicQuestionResult> getAdmissionBasicSign(@RequestParam("admissionId") Integer admissionID,
                                                                               HttpServletRequest request,
                                                                               HttpServletResponse response) {
        log.setLevel(Level.INFO);
        //确保是正数
        if (admissionID <= 0) return CommonResult.fail("传入数据格式错误");
        try {
            return COrganizationInterviewService.getAdmissionBasicSign(admissionID, request, response);
        } catch (Exception e) {
            e.printStackTrace();
            if ("Cannot parse null string".equals(e.getMessage()) || "null".equals(e.getMessage()))
                return CommonResult.loginError();
            return CommonResult.fail();
        }
    }

    /**
     * 获取社团面试基本问题
     *
     * @param admissionID 纳新id
     * @return 社团面试轮次返回体
     */
    @RequestMapping(value = "/organization/question-answer", method = RequestMethod.GET)
    public CommonResult<OrganizationQuestionAnswerResult> getOrganizationQuestionAnswer(@RequestParam("admissionId") Integer admissionID,
                                                                                        HttpServletRequest request,
                                                                                        HttpServletResponse response) {
        log.setLevel(Level.INFO);
        //确保是正数
        if (admissionID <= 0) return CommonResult.fail("传入数据格式错误");
        try {
            return COrganizationInterviewService.getOrganizationQuestionAnswer(admissionID, request, response);
        } catch (Exception e) {
            e.printStackTrace();
            if ("Cannot parse null string".equals(e.getMessage()) || "null".equals(e.getMessage()))
                return CommonResult.loginError();
            return CommonResult.fail();
        }
    }

    /**
     * 设置社团面试通知消息控制层
     *
     * @param admissionID 纳新id
     * @return 通用返回
     */
    @RequestMapping(value = "/c/organization/interview/message", method = RequestMethod.POST)
    public CommonResult<NoData> InterviewMessageC(@RequestParam("admissionId") Integer admissionID,
                                                  HttpServletRequest request, HttpServletResponse response) {
        log.setLevel(Level.INFO);
        //确保是正数
        if (admissionID <= 0) return CommonResult.fail("传入数据格式错误");
        try {
            return COrganizationInterviewService.interviewMessageC(admissionID, request, response);
        } catch (Exception e) {
            e.printStackTrace();
            if ("Cannot parse null string".equals(e.getMessage()) || "null".equals(e.getMessage()))
                return CommonResult.loginError();
            return CommonResult.fail();
        }
    }

    /**
     * 校验数据
     *
     * @return 校验信息
     */
    private String CheckData(BindingResult result) {
        if (result.hasErrors()) {
            StringBuilder message = new StringBuilder(ResultEnum.FAILED.getMessage());
            List<ObjectError> list = result.getAllErrors();
            for (ObjectError objectError : list) {
                message.append("---").append(objectError.getDefaultMessage());
                log.warning(objectError.getDefaultMessage());
            }
            return message.toString();
        }
        return null;
    }

}

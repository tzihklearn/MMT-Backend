package com.sipc.mmtbackend.controller.c;

import com.sipc.mmtbackend.pojo.c.param.InterviewCheckInParam;
import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.service.c.InterviewCheckInService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/c/interview")
public class InterviewCheckInController {
    @Resource
    InterviewCheckInService interviewCheckInService;

    @Resource
    private HttpServletRequest request;

    @Resource
    private HttpServletResponse response;

    /**
     * C 端签到
     * @param param 二维码载荷
     * @return 签到结构
     */
    @PostMapping("/verify")
    CommonResult<String> interviewCheckin(@RequestBody InterviewCheckInParam param){
        return interviewCheckInService.interviewCheckin(request, response, param);
    }
}

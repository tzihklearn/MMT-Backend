package com.sipc.mmtbackend.service.c;

import com.sipc.mmtbackend.pojo.c.param.InterviewCheckInParam;
import com.sipc.mmtbackend.pojo.dto.CommonResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface InterviewCheckInService {
    /**
     * C 端签到
     * @param param 二维码载荷
     * @return 签到结构
     */
    CommonResult interviewCheckin(HttpServletRequest request, HttpServletResponse response, InterviewCheckInParam param);
}

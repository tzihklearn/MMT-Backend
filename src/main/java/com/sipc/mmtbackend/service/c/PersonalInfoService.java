package com.sipc.mmtbackend.service.c;

import com.sipc.mmtbackend.pojo.c.param.SecondUpdateParam;
import com.sipc.mmtbackend.pojo.c.result.PersonalInfoArrangeBackResult;
import com.sipc.mmtbackend.pojo.c.result.UserInfoResult;
import com.sipc.mmtbackend.pojo.dto.CommonResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface PersonalInfoService {
    CommonResult<UserInfoResult> getPersonalInfo(HttpServletRequest request, HttpServletResponse response);

    CommonResult<String> updatePersonalInfo(HttpServletRequest request, HttpServletResponse response, SecondUpdateParam secondUpdateParam);

    CommonResult<String> insertPersonalInfo(HttpServletRequest request, HttpServletResponse response, UserInfoResult userInfoResult);

    CommonResult<PersonalInfoArrangeBackResult> getPersonalArrangeInfo(HttpServletRequest request, HttpServletResponse response) throws IOException;

    CommonResult<String> getUrl(HttpServletRequest request, HttpServletResponse response) throws IOException;
}

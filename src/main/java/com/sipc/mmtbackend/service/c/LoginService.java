package com.sipc.mmtbackend.service.c;

import com.sipc.mmtbackend.pojo.c.param.JsCodeParam;
import com.sipc.mmtbackend.pojo.c.result.NoData;
import com.sipc.mmtbackend.pojo.dto.CommonResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.NoSuchAlgorithmException;

public interface LoginService {

    void addLogCookie(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, String studentId, String cookieName, Integer organizationId);

    CommonResult<NoData> loginC(JsCodeParam jscodeParam, HttpServletRequest request, HttpServletResponse response );


}

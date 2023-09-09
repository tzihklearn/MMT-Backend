package com.sipc.mmtbackend.service.c;

import com.sipc.mmtbackend.pojo.c.param.IsCertificationParam;
import com.sipc.mmtbackend.pojo.dto.CommonResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface UpdateUserInfoService {
    CommonResult<IsCertificationParam> updateUserInfo(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse);
}

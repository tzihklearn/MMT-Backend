package com.sipc.mmtbackend.controller.c;

import com.sipc.mmtbackend.pojo.c.param.IsCertificationParam;
import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.service.c.UpdateUserInfoService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@Validated
public class UpdateUserInfoController {
    @Resource
    UpdateUserInfoService updateUserInfoService;

    @PostMapping("/student/info/all")
    public CommonResult<IsCertificationParam> updateUserInfo(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        return updateUserInfoService.updateUserInfo(httpServletRequest, httpServletResponse);
    }
}

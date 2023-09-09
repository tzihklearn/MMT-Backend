package com.sipc.mmtbackend.controller.c;

import com.sipc.mmtbackend.pojo.c.param.SecondUpdateParam;
import com.sipc.mmtbackend.pojo.c.result.PersonalInfoArrangeBackResult;
import com.sipc.mmtbackend.pojo.c.result.UserInfoResult;
import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.service.c.PersonalInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@Validated
public class PersonalInfoController {
    private final PersonalInfoService personalInfoService;

    @Autowired
    public PersonalInfoController(PersonalInfoService service) {
        this.personalInfoService = service;
    }

    @GetMapping("/c/personal-info/head/url")
    public CommonResult<String> getUrl(HttpServletRequest request, HttpServletResponse response) throws IOException {
        return personalInfoService.getUrl(request, response);
    }

    @PostMapping("/c/personal-info/all")
    public CommonResult<UserInfoResult> getPersonalInfo(HttpServletRequest request, HttpServletResponse response) {
        return personalInfoService.getPersonalInfo(request, response);
    }

    @PostMapping("/c/personal-info/arrange")
    public CommonResult<PersonalInfoArrangeBackResult> getPersonalArrangeInfo(HttpServletRequest request, HttpServletResponse response) throws IOException {
        return personalInfoService.getPersonalArrangeInfo(request, response);
    }

    @PostMapping("/c/personal-info/second")
    public CommonResult<String> updatePersonalInfo(@RequestBody SecondUpdateParam secondUpdateParam, HttpServletRequest request, HttpServletResponse response) {
        return personalInfoService.updatePersonalInfo(request, response, secondUpdateParam);
    }

    @PostMapping("/c/personal-info/insert")
    public CommonResult<String> insertPersonalInfo(@RequestBody UserInfoResult userInfoResult, HttpServletRequest request, HttpServletResponse response) {
        return personalInfoService.insertPersonalInfo(request, response, userInfoResult);
    }
}

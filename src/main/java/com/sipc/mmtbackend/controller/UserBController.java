package com.sipc.mmtbackend.controller;

import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.pojo.dto.param.UserBParam.LoginPassParam;
import com.sipc.mmtbackend.pojo.dto.param.UserBParam.RegParam;
import com.sipc.mmtbackend.pojo.dto.result.UserBResult.LoginResult;
import com.sipc.mmtbackend.pojo.exceptions.DateBaseException;
import com.sipc.mmtbackend.service.UserBService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/b/userb")
public class UserBController {
    @Resource
    UserBService userBService;
    @PostMapping("/reg")
    public CommonResult<String> registUser(@RequestBody RegParam param){
        try {
            return userBService.registUser(param);
        } catch (DateBaseException e) {
            return CommonResult.fail(e.getMessage());
        }
    }
    @PostMapping("/loginp")
    public CommonResult<LoginResult> loginByPass(LoginPassParam param){
        return userBService.loginByPass(param);
    }
}

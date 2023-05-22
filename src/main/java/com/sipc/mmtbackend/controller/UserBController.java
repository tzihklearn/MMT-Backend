package com.sipc.mmtbackend.controller;

import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.pojo.dto.param.UserBParam.LoginPassParam;
import com.sipc.mmtbackend.pojo.dto.param.UserBParam.RegParam;
import com.sipc.mmtbackend.pojo.dto.result.UserBResult.JoinOrgsResult;
import com.sipc.mmtbackend.pojo.dto.result.UserBResult.LoginResult;
import com.sipc.mmtbackend.pojo.exceptions.DateBaseException;
import com.sipc.mmtbackend.service.UserBService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.format.SignStyle;

@RestController
@RequestMapping("/b/user")
public class UserBController {
    @Resource
    UserBService userBService;

    /**
     * B端注册
     * @param param 注册信息
     * @return 状态信息
     */
    @PostMapping("/reg")
    public CommonResult<String> registUser(@RequestBody RegParam param){
        try {
            return userBService.registUser(param);
        } catch (DateBaseException e) {
            return CommonResult.fail(e.getMessage());
        }
    }

    @GetMapping("/orgs")
    public CommonResult<JoinOrgsResult> getJoinedOrgs(@RequestParam("studentId") String studentId){
        return userBService.getJoinedOrgs(studentId);
    }

    /**
     * 使用学号与密码登录
     * @param param 学号与密码
     * @return token、用户ID与
     */
    @PostMapping("/loginp")
    public CommonResult<LoginResult> loginByPass(@RequestBody LoginPassParam param){
        return userBService.loginByPass(param);
    }
}

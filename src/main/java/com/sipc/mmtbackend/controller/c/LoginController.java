package com.sipc.mmtbackend.controller.c;

import com.sipc.mmtbackend.pojo.c.param.JsCodeParam;
import com.sipc.mmtbackend.pojo.c.result.NoData;
import com.sipc.mmtbackend.pojo.dto.CommonResult;

import com.sipc.mmtbackend.service.c.LoginService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.security.NoSuchAlgorithmException;

/**
 * @author 徐嘉豪
 * 登录
 */

@RestController
public class LoginController {

    @Resource
    LoginService loginService;
    @Resource
    private HttpServletRequest httpServletRequest;
    @Resource
    private HttpServletResponse httpServletResponse;

    /**
     * C端登录
     * @param jsCode
     * @return
     * 授权成功之后添加cookie，openid=xx
     */
    @PostMapping("/c/login")
    public CommonResult<NoData> loginC(@NotNull(message = "jsCode不能为空") @RequestBody() @Validated JsCodeParam jsCode) {
        return loginService.loginC(jsCode,httpServletRequest, httpServletResponse);
    }
}

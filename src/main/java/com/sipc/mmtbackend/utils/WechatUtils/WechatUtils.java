package com.sipc.mmtbackend.utils.WechatUtils;

import com.sipc.mmtbackend.utils.WechatUtils.dto.AuthorizationResult;
import com.sipc.mmtbackend.utils.request.RequestUtils;
import com.sipc.mmtbackend.utils.request.dto.Param;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "wechat")
public class WechatUtils {
    /**
     * 小程序 appId
     */
    private String appid;

    /**
     * 小程序 appSecret
     */
    private String secret;

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    /**
     * 微信登陆，通过js_code换取session
     *
     * @param js_code 前端登陆的js_code
     * @return 返回登陆结果
     */
    public AuthorizationResult js_codeToSession(String js_code) {

        return  RequestUtils.GET(
                "https://api.weixin.qq.com/sns/jscode2session",
                new Param(
                        "appid", appid,
                        "secret", secret,
                        "js_code", js_code,
                        "grant_type", "authorization_code"),
                AuthorizationResult.class);
    }

}

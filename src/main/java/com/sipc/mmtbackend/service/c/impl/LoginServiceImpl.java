package com.sipc.mmtbackend.service.c.impl;

import com.sipc.mmtbackend.common.Constant;
import com.sipc.mmtbackend.mapper.AdmissionMapper;
import com.sipc.mmtbackend.mapper.OrganizationMapper;
import com.sipc.mmtbackend.mapper.UserInfoMapper;
import com.sipc.mmtbackend.mapper.c.LoginStatusMapper;
import com.sipc.mmtbackend.mapper.c.UserCMapper;
import com.sipc.mmtbackend.pojo.c.domain.LoginStatus;
import com.sipc.mmtbackend.pojo.c.domain.UserC;
import com.sipc.mmtbackend.pojo.c.param.JsCodeParam;
import com.sipc.mmtbackend.pojo.c.result.NoData;
import com.sipc.mmtbackend.pojo.domain.UserInfo;
import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.pojo.exceptions.ErrorException;
import com.sipc.mmtbackend.service.c.LoginService;
import com.sipc.mmtbackend.utils.GetRandomString;
import com.sipc.mmtbackend.utils.RedisUtil;
import com.sipc.mmtbackend.utils.WechatUtils.WechatUtils;
import com.sipc.mmtbackend.utils.WechatUtils.dto.AuthorizationResult;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

@Service
public class LoginServiceImpl implements LoginService {

    @Resource
    UserCMapper userCMapper;
    @Resource
    LoginStatusMapper loginStatusMapper;
    @Resource
    RedisUtil redisUtil;
    @Resource
    UserInfoMapper userInfoMapper;

    @Override
    public CommonResult<NoData> loginC(JsCodeParam jscodeParam, HttpServletRequest request, HttpServletResponse response) {

        //初始化微信包
        WechatUtils wechatUtils = new WechatUtils();
        wechatUtils.setAppid(Constant.APPID);
        wechatUtils.setSecret(Constant.SECRET);
        //调用微信包拿到认证信息
        AuthorizationResult authorizationResult = wechatUtils.js_codeToSession(jscodeParam.getJsCode());

//        authorizationResult = new AuthorizationResult(); // 测试用

        if(authorizationResult == null) {
            throw new ErrorException("微信授权失败");
        }

        //拿到openid
        String openid = authorizationResult.getOpenid();

//        openid = "111"; //测试用

        if(openid == null) {
            throw new ErrorException("微信授权失败");
        }

        UserC userByOpenid = userCMapper.getUserByOpenid(openid);

        boolean isFirst = false;

        //验证用户是否为第一次登录
        if(userByOpenid == null) {
            isFirst = true;
        }

        if(isFirst) {
            UserC user = new UserC();
            user.setOpenid(openid);
            int i = userCMapper.insertSelective(user);

            if(i == 0) {
                throw new ErrorException("数据库插入异常");
            }

            UserC userByOpenid1 = userCMapper.getUserByOpenid(openid);

            if(userByOpenid1 == null) {
                return CommonResult.fail("数据库异常");
            }

            UserInfo userInfo = new UserInfo();
            userInfo.setId(userByOpenid1.getId());

            UserInfo userInfo1 = userInfoMapper.selectById(userInfo.getId());

            if(userInfo1 != null) {
                throw new ErrorException("数据库异常");
            }

            userInfoMapper.insert(userInfo);
        }

        //设置cookie
        addLogCookie(request, response, openid, Constant.COOKIE_C, null);

        if(isFirst) {
            return CommonResult.success("用户微信第一次登录");
        }
        return CommonResult.success();
    }

    /**
     * 一切正常的话，签发cookie,cookie存活时间为12小时，redis中也为12小时
     *
     * @param request
     * @param httpServletResponse
     * @param studentId
     * @param cookieName
     */
    @Override
    public synchronized void addLogCookie(HttpServletRequest request, HttpServletResponse httpServletResponse, String studentId, String cookieName, Integer organizationId) {
        LoginStatus byValue = loginStatusMapper.selectByValue(studentId);
        String userAgent = request.getHeader("USER-AGENT");

        //已在其他端登录
        if (byValue != null) {
            //在数据库中删除登录状态
            loginStatusMapper.deleteByValue(studentId);
            //在redis中删除原来的登录状态
            String key = byValue.getKey();
            redisUtil.remove(key);
        }
        //生成随机key字符串长度为16
        String key = GetRandomString.getRandomString2(16);

        //避免key重复覆盖
        while (redisUtil.exists(key)) {
            key = GetRandomString.getRandomString2(16);
        }

        //redis中维护登录状态
        redisUtil.set(key, studentId, (long) 12, TimeUnit.HOURS);


        //MySQL中维护登录状态
        //完善记录
        LoginStatus loginStatus = new LoginStatus();
        loginStatus.setKey(key);
        loginStatus.setValue(studentId);
        loginStatus.setUserAgent(userAgent);

        //如果是b端
        if (Constant.COOKIE_B.equals(cookieName) && organizationId != null) {
            //添加登录社团
            loginStatus.setOrganizationId(organizationId);
            loginStatusMapper.insertWithOrgId(loginStatus);
        } else {
            //插入MySQL表
            loginStatusMapper.insert(loginStatus);
        }
        //设置cookie
        Cookie studentId1 = new Cookie(cookieName, key);
        studentId1.setMaxAge(60 * 12 * 60);
        studentId1.setPath("/");
//        studentId1.setHttpOnly(false);
//        studentId1.setSecure(true);

        //添加cookie
        httpServletResponse.addCookie(studentId1);
        httpServletResponse.setHeader("Access-Control-Allow-Credentials", "true");

    }

}


package com.sipc.mmtbackend.service;

import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.pojo.dto.param.UserBParam.*;
import com.sipc.mmtbackend.pojo.dto.result.UserBResult.GetBUserInfoResult;
import com.sipc.mmtbackend.pojo.dto.result.UserBResult.JoinOrgsResult;
import com.sipc.mmtbackend.pojo.dto.result.UserBResult.LoginResult;
import com.sipc.mmtbackend.pojo.dto.result.UserBResult.SwitchOrgResult;
import com.sipc.mmtbackend.pojo.exceptions.DateBaseException;
import net.sf.jsqlparser.util.validation.metadata.DatabaseException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface UserBService {
    /**
     * B 端用户注册
     *
     * @param param 用户信息、邀请码
     * @return 注册结果
     * @throws DatabaseException 数据库异常
     * @author DoudiNCer
     */
    CommonResult<String> registUser(RegParam param) throws DateBaseException;

    /**
     * B 端账号登陆
     *
     * @param param 用户帐号（学号）、登录组织ID、密码
     * @return 登录结果、简单的用户信息、Token
     * @author DoudiNCer
     */
    CommonResult<LoginResult> loginByPass(LoginPassParam param);

    /**
     * 获取 B 端用户加入组织列表
     *
     * @param studentId 用户账号（学号）
     * @return 加入的组织列表
     * @author DoudiNCer
     */
    CommonResult<JoinOrgsResult> getJoinedOrgs(String studentId);

    /**
     * 获取 B 端用户信息
     *
     * @param request  HTTP请求报文
     * @param response HTTP响应报文
     * @return 用户信息
     * @author DoudiNCer
     */
    CommonResult<GetBUserInfoResult> getUserInfo(HttpServletRequest request, HttpServletResponse response);

    /**
     * 更新 B 端用户密码
     *
     * @param request  HTTP 请求报文
     * @param response HTTP 响应报文
     * @param param    旧密码与新密码
     * @return 处理结果
     * @author DoudiNCer
     */
    CommonResult<String> putUserNewPassword(HttpServletRequest request, HttpServletResponse response, PutUserPasswordParam param);

    /**
     * B 端用户登出
     *
     * @param request  HTTP请求报文
     * @param response HTTP响应报文
     * @return 处理结果
     */
    CommonResult<String> logout(HttpServletRequest request, HttpServletResponse response);

    /**
     * B 端用户切换组织
     *
     * @param request  HTTP请求报文
     * @param response HTTP响应报文
     * @param param    要切换的组织
     * @return 权限信息、新 Token
     */
    CommonResult<SwitchOrgResult> switchOrganization(HttpServletRequest request, HttpServletResponse response, SwitchOrgParam param);

    /**
     * B 端加入新组织
     *
     * @param request  HTTP 请求报文
     * @param response HTTP 响应报文
     * @param param    邀请码与密码
     * @return 处理结果
     */
    CommonResult<String> addNewOrganization(HttpServletRequest request, HttpServletResponse response, AddNewOrgParam param) throws DatabaseException;
}

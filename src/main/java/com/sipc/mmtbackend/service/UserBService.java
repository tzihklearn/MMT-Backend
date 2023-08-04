package com.sipc.mmtbackend.service;

import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.pojo.dto.param.UserBParam.*;
import com.sipc.mmtbackend.pojo.dto.result.UserBResult.*;
import com.sipc.mmtbackend.pojo.exceptions.DateBaseException;
import net.sf.jsqlparser.util.validation.metadata.DatabaseException;
import org.springframework.web.multipart.MultipartFile;

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
     * @return 用户信息
     * @author DoudiNCer
     */
    CommonResult<GetBUserInfoResult> getUserInfo();

    /**
     * 更新 B 端用户密码
     *
     * @param param 旧密码与新密码
     * @return 处理结果
     * @author DoudiNCer
     */
    CommonResult<String> putUserNewPassword(PutUserPasswordParam param);

    /**
     * B 端用户登出
     *
     * @return 处理结果
     */
    CommonResult<String> logout();

    /**
     * B 端用户切换组织
     *
     * @param param 要切换的组织
     * @return 权限信息、新 Token
     */
    CommonResult<SwitchOrgResult> switchOrganization(SwitchOrgParam param);

    /**
     * B 端加入新组织
     *
     * @param param 邀请码与密码
     * @return 处理结果
     */
    CommonResult<String> addNewOrganization(AddNewOrgParam param) throws DatabaseException;

    /**
     * B 端用户更新头像
     *
     * @param avatar 头像文件
     * @return 处理结果，包含新头像的 URL
     * @author DoudiNCer
     */
    CommonResult<PutUserAvatarResult> putUserAvatar(MultipartFile avatar);

    /**
     * 登录后获取已加入的组织及当前登录的组织
     * @return 用户已加入的组织及是否为当前组织
     */
    CommonResult<LoginedJoinOrgsResult> getLoginedJoinedOrgs();

    /**
     * B 端反馈
     *
     * @param param 用户名、邮箱与反馈信息
     * @return 反馈结果
     */
    CommonResult<String> feedback(BFeedbackParam param);
}

package com.sipc.mmtbackend.controller;

import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.pojo.dto.param.UserBParam.*;
import com.sipc.mmtbackend.pojo.dto.result.UserBResult.*;
import com.sipc.mmtbackend.pojo.exceptions.DateBaseException;
import com.sipc.mmtbackend.service.UserBService;
import net.sf.jsqlparser.util.validation.metadata.DatabaseException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

@RestController
@RequestMapping("/b/user")
public class UserBController {
    @Resource
    UserBService userBService;

    /**
     * B端注册
     *
     * @param param 注册信息
     * @return 状态信息
     */
    @PostMapping("/reg")
    public CommonResult<String> registUser(@RequestBody RegParam param) {
        try {
            return userBService.registUser(param);
        } catch (DateBaseException e) {
            return CommonResult.fail(e.getMessage());
        }
    }

    @GetMapping("/orgs")
    public CommonResult<JoinOrgsResult> getJoinedOrgs(@RequestParam("studentId") String studentId) {
        return userBService.getJoinedOrgs(studentId);
    }

    /**
     * 使用学号与密码登录
     *
     * @param param 学号与密码
     * @return token、用户ID与
     */
    @PostMapping("/loginp")
    public CommonResult<LoginResult> loginByPass(@RequestBody LoginPassParam param) {
        return userBService.loginByPass(param);
    }

    /**
     * B 端获取用户信息
     *
     * @return 用户信息
     */
    @GetMapping("/userinfo")
    public CommonResult<GetBUserInfoResult> getUserInfo() {
        return userBService.getUserInfo();
    }

    /**
     * B 端用户更新头像
     *
     * @param avatar 头像文件
     * @return 处理结果，包含新头像的 URL
     * @author DoudiNCer
     */
    @PutMapping("/avatar")
    public CommonResult<PutUserAvatarResult> putUserAvatar(@RequestPart("avatar") MultipartFile avatar) {
        return userBService.putUserAvatar(avatar);
    }

    /**
     * 更新 B 端用户密码
     *
     * @param param 旧密码与新密码
     * @return 处理结果
     * @author DoudiNCer
     */
    @PutMapping("/password")
    public CommonResult<String> changeUserNewPassword(PutUserPasswordParam param) {
        return userBService.putUserNewPassword(param);
    }

    /**
     * B 端用户登出
     *
     * @return 处理结果
     */
    @PostMapping("/logout")
    public CommonResult<String> logout() {
        return userBService.logout();
    }

    /**
     * B 端用户切换组织
     *
     * @param param 要切换的组织
     * @return 权限信息、新 Token
     */
    @PutMapping("/switchOrg")
    public CommonResult<SwitchOrgResult> switchOrganization(SwitchOrgParam param) {
        return userBService.switchOrganization(param);
    }

    /**
     * B 端加入新组织
     *
     * @param param 邀请码与密码
     * @return 处理结果
     */
    @PostMapping("/addNewOrg")
    public CommonResult<String> addNewOrganization(AddNewOrgParam param) {
        try {
            return userBService.addNewOrganization(param);
        } catch (DatabaseException e) {
            return CommonResult.fail(e.getMessage());
        }
    }
}

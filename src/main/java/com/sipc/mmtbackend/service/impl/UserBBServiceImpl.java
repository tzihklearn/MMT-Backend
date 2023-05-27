package com.sipc.mmtbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sipc.mmtbackend.mapper.RoleMapper;
import com.sipc.mmtbackend.mapper.UserBMapper;
import com.sipc.mmtbackend.mapper.UserBRoleMapper;
import com.sipc.mmtbackend.mapper.UserRoleMergeMapper;
import com.sipc.mmtbackend.pojo.domain.Role;
import com.sipc.mmtbackend.pojo.domain.UserB;
import com.sipc.mmtbackend.pojo.domain.UserRoleMerge;
import com.sipc.mmtbackend.pojo.domain.po.UserBRole.JoinedOrgPo;
import com.sipc.mmtbackend.pojo.domain.po.UserBRole.UserLoginPermissionPo;
import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.pojo.dto.param.UserBParam.*;
import com.sipc.mmtbackend.pojo.dto.result.UserBResult.*;
import com.sipc.mmtbackend.pojo.dto.result.UserBResult.po.JoinedOrgResultPo;
import com.sipc.mmtbackend.pojo.dto.resultEnum.ResultEnum;
import com.sipc.mmtbackend.service.UserBService;
import com.sipc.mmtbackend.utils.CheckroleBUtil.CheckRoleUtil;
import com.sipc.mmtbackend.utils.CheckroleBUtil.JWTUtil;
import com.sipc.mmtbackend.utils.CheckroleBUtil.PasswordUtil;
import com.sipc.mmtbackend.utils.CheckroleBUtil.pojo.BTokenSwapPo;
import com.sipc.mmtbackend.utils.CheckroleBUtil.pojo.CheckRoleResult;
import com.sipc.mmtbackend.utils.CheckroleBUtil.pojo.PermissionEnum;
import com.sipc.mmtbackend.utils.ICodeUtil;
import com.sipc.mmtbackend.utils.PictureUtil.PictureUtil;
import com.sipc.mmtbackend.utils.PictureUtil.pojo.DefaultPictureIdEnum;
import com.sipc.mmtbackend.utils.PictureUtil.pojo.PictureUsage;
import com.sipc.mmtbackend.utils.PictureUtil.pojo.UsageEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.util.validation.metadata.DatabaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Slf4j
public class UserBBServiceImpl implements UserBService {
    private final RoleMapper roleMapper;
    private final UserBMapper userBMapper;
    private final UserBRoleMapper userBRoleMapper;
    private final UserRoleMergeMapper userRoleMergeMapper;
    private final ICodeUtil iCodeUtil;
    private final JWTUtil jwtUtil;
    private final CheckRoleUtil checkRoleUtil;
    private final PictureUtil pictureUtil;

    /**
     * B 端用户注册
     *
     * @param param 用户信息、邀请码
     * @return 注册结果
     * @throws DatabaseException 数据库异常
     * @author DoudiNCer
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<String> registUser(RegParam param) throws DatabaseException {
        Integer orgId = iCodeUtil.verifyICode(param.getKey());
        // 科协测试邀请码
        if (Objects.equals(param.getKey(), "qwertyuiop"))
            orgId = 1;
        if (orgId == null)
            return CommonResult.fail("注册失败：邀请码无效");
        UserB userB = userBMapper.selectOne(new QueryWrapper<UserB>().eq("phone", param.getPhoneNum()));
        if (userB != null) {
            log.info("用户" + param + "注册手机号重复：" + userB);
            return CommonResult.fail("注册失败：手机号重复");
        }
        userB = userBMapper.selectOne(new QueryWrapper<UserB>().eq("student_id", param.getStudentId()));
        if (userB != null) {
            log.info("用户" + param + "注册学号重复：" + userB);
            return CommonResult.fail("注册失败：学号重复");
        }
        // 初始化一个新组织的新角色
        Role role = roleMapper.selectOne(new QueryWrapper<Role>().eq("organization_id", orgId).eq("permission_id", 3));
        if (role == null) {
            role = new Role();
            role.setOrganizationId(orgId);
            role.setPermissionId(3);
            int insert = roleMapper.insert(role);
            if (insert != 1) {
                log.warn("初始化组织 " + orgId + " 的角色时失败，受影响行数：" + insert);
                throw new DatabaseException("数据库错误");
            }
        }
        UserB user = new UserB();
        UserRoleMerge roleMerge = new UserRoleMerge();
        user.setPhone(param.getPhoneNum());
        user.setStudentId(param.getStudentId());
        user.setUserName(param.getUsername());
        user.setEmail(param.getEmail());
        int uins = userBMapper.insert(user);
        if (uins != 1) {
            log.error("创建B端用户" + user + "失败, 受影响行数：" + uins);
            throw new DatabaseException("创建用户失败：数据库错误");
        }
        roleMerge.setRoleId(role.getId());
        roleMerge.setUserId(user.getId());
        roleMerge.setPassword(PasswordUtil.hashPassword(param.getPassword()));
        int urint = userRoleMergeMapper.insert(roleMerge);
        if (urint != 1) {
            log.warn("创建 B 端用户 " + user + " 的角色" + roleMerge + "失败，受影响行数：" + urint);
            throw new DatabaseException("创建用户失败：数据库错误");
        }
        return CommonResult.success();
    }

    /**
     * B 端账号登陆
     *
     * @param param 用户帐号（学号）、登录组织ID、密码
     * @return 登录结果、简单的用户信息、Token
     * @author DoudiNCer
     */
    @Override
    public CommonResult<LoginResult> loginByPass(LoginPassParam param) {
        UserLoginPermissionPo userLoginPermission = userBRoleMapper.selectBUserLoginInfoByStudentIdAndOrgId(param.getStudentId(), param.getOrganizationId());
        if (userLoginPermission == null)
            return CommonResult.fail("用户名或密码错误");
        if (!PasswordUtil.testPasswd(param.getPassword(), userLoginPermission.getPassword()))
            return CommonResult.fail("用户名或密码错误");
        String token = jwtUtil.createToken(new BTokenSwapPo(userLoginPermission));
        LoginResult result = new LoginResult();
        result.setUserId(userLoginPermission.getUserId());
        result.setToken(token);
        result.setUsername(userLoginPermission.getUsername());
        result.setPermissionId(userLoginPermission.getPermissionId());
        result.setPermissionName(userLoginPermission.getPermissionName());
        return CommonResult.success(result);
    }

    /**
     * 获取 B 端用户加入组织列表
     *
     * @param studentId 用户账号（学号）
     * @return 加入的组织列表
     * @author DoudiNCer
     */
    @Override
    public CommonResult<JoinOrgsResult> getJoinedOrgs(String studentId) {
        UserB userB = userBMapper.selectOne(new QueryWrapper<UserB>().eq("student_id", studentId));
        if (userB == null)
            return CommonResult.fail("用户不存在");
        List<JoinedOrgResultPo> results = new LinkedList<>();
        for (JoinedOrgPo joinedOrgPo : userBRoleMapper.selectJoinedOrganizationsByBUserId(userB.getId())) {
            JoinedOrgResultPo result = new JoinedOrgResultPo();
            result.setOrganizationName(joinedOrgPo.getOrganizationName());
            result.setOrganizationId(joinedOrgPo.getOrganizationId());
            results.add(result);
        }
        JoinOrgsResult result = new JoinOrgsResult();
        result.setNum(results.size());
        result.setOrganizations(results);
        return CommonResult.success(result);
    }

    /**
     * 获取 B 端用户信息
     *
     * @param request  HTTP请求报文
     * @param response HTTP响应报文
     * @return 用户信息
     * @author DoudiNCer
     */
    @Override
    public CommonResult<GetBUserInfoResult> getUserInfo(HttpServletRequest request, HttpServletResponse response) {
        CommonResult<CheckRoleResult> check = checkRoleUtil.check(request, response);
        if (!Objects.equals(check.getCode(), ResultEnum.SUCCESS.getCode()))
            return CommonResult.fail(check.getCode(), check.getMessage());
        CheckRoleResult data = check.getData();
        GetBUserInfoResult result = new GetBUserInfoResult();
        UserB userB = userBMapper.selectById(data.getUserId());
        if (userB == null) {
            log.warn("获取 B 端用户信息失败：用户不存在，Token解析结果：" + data);
            return CommonResult.fail("数据库错误");
        }
        result.setUserId(data.getUserId());
        result.setUsername(data.getUsername());
        result.setStudentId(data.getStudentId());
        result.setOrganizationId(data.getOrganizationId());
        result.setOrganizationName(data.getOrganizationName());
        result.setPermissionId(data.getPermissionId());
        result.setPermissionName(data.getPermissionName());
        // 对手机号进行脱敏
        StringBuilder sb = new StringBuilder();
        String phone = userB.getPhone();
        sb.append(phone, 0, 3);
        sb.append(" **** ");
        sb.append(phone.substring(7));
        result.setPhone(sb.toString());
        // 获取头像链接
        String avatarUrl;
        if (userB.getAvatarId() == null || userB.getAvatarId().length() == 0)
            avatarUrl = pictureUtil.getPictureURL(DefaultPictureIdEnum.B_USER_AVATAR.getPictureId());
        else
            avatarUrl = pictureUtil.getPictureURL(userB.getAvatarId());
        result.setAvatarUrl(avatarUrl);
        return CommonResult.success(result);
    }

    /**
     * 更新 B 端用户密码
     *
     * @param request  HTTP 请求报文
     * @param response HTTP 响应报文
     * @param param    旧密码与新密码
     * @return 处理结果
     * @author DoudiNCer
     */
    @Override
    public CommonResult<String> putUserNewPassword(HttpServletRequest request, HttpServletResponse response, PutUserPasswordParam param) {
        CommonResult<CheckRoleResult> check = checkRoleUtil.check(request, response);
        if (!Objects.equals(check.getCode(), ResultEnum.SUCCESS.getCode()))
            return CommonResult.fail(check.getCode(), check.getMessage());
        CheckRoleResult data = check.getData();
        UserRoleMerge userRoleMerge = userRoleMergeMapper.selectOne(
                new QueryWrapper<UserRoleMerge>()
                        .eq("user_id", data.getUserId())
                        .eq("role_id", data.getRoleId())
        );
        if (userRoleMerge == null) {
            log.warn("获取 B 端用户信息失败：用户不存在，Token解析结果：" + data);
            return CommonResult.fail("数据库错误");
        }
        boolean testPasswd = PasswordUtil.testPasswd(param.getOldPassword(), userRoleMerge.getPassword());
        if (!testPasswd) {
            return CommonResult.fail("旧密码错误");
        }
        userRoleMerge.setPassword(PasswordUtil.hashPassword(param.getNewPassword()));
        int updateById = userRoleMergeMapper.updateById(userRoleMerge);
        if (updateById != -1) {
            log.warn("更新用户 " + userRoleMerge + " 密码错误，受影响行数：" + updateById);
            return CommonResult.fail("数据库错误");
        }
        return CommonResult.success();
    }

    /**
     * B 端用户登出
     *
     * @param request  HTTP请求报文
     * @param response HTTP响应报文
     * @return 处理结果
     */
    @Override
    public CommonResult<String> logout(HttpServletRequest request, HttpServletResponse response) {
        CommonResult<CheckRoleResult> check = checkRoleUtil.check(request, response);
        if (!Objects.equals(check.getCode(), ResultEnum.SUCCESS.getCode()))
            return CommonResult.fail(check.getCode(), check.getMessage());
        return checkRoleUtil.logout(request, response);
    }

    /**
     * B 端用户切换组织
     *
     * @param request  HTTP请求报文
     * @param response HTTP响应报文
     * @param param    要切换的组织
     * @return 权限信息、新 Token
     */
    @Override
    public CommonResult<SwitchOrgResult> switchOrganization(HttpServletRequest request, HttpServletResponse response, SwitchOrgParam param) {
        CommonResult<CheckRoleResult> check = checkRoleUtil.check(request, response);
        if (!Objects.equals(check.getCode(), ResultEnum.SUCCESS.getCode()))
            return CommonResult.fail(check.getCode(), check.getMessage());
        CheckRoleResult data = check.getData();
        UserLoginPermissionPo userLoginPermission = userBRoleMapper.selectBUserLoginInfoByStudentIdAndOrgId(data.getStudentId(), param.getOrganizationId());
        if (userLoginPermission == null)
            return CommonResult.fail("切换组织错误：组织不存在");
        CommonResult<String> logout = checkRoleUtil.logout(request, response);
        if (!Objects.equals(logout.getCode(), ResultEnum.SUCCESS.getCode()))
            return CommonResult.fail(logout.getCode(), logout.getMessage());
        String token = jwtUtil.createToken(new BTokenSwapPo(userLoginPermission));
        SwitchOrgResult result = new SwitchOrgResult();
        result.setPermissionId(userLoginPermission.getPermissionId());
        result.setPermissionName(userLoginPermission.getPermissionName());
        result.setToken(token);
        return CommonResult.success(result);
    }

    /**
     * B 端加入新组织
     *
     * @param request  HTTP 请求报文
     * @param response HTTP 响应报文
     * @param param    邀请码与密码
     * @return 处理结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<String> addNewOrganization(HttpServletRequest request, HttpServletResponse response, AddNewOrgParam param) throws DatabaseException {
        CommonResult<CheckRoleResult> check = checkRoleUtil.check(request, response);
        if (!Objects.equals(check.getCode(), ResultEnum.SUCCESS.getCode()))
            return CommonResult.fail(check.getCode(), check.getMessage());
        CheckRoleResult data = check.getData();
        if (data.getPermissionId() < PermissionEnum.COMMITTEE.getId())
            return CommonResult.fail("Super Admin 不允许加入其他组织");
        Integer orgId = iCodeUtil.verifyICode(param.getKey());
        // 科协测试邀请码
        if (Objects.equals(param.getKey(), "qwertyuiop"))
            orgId = 1;
        if (orgId == null)
            return CommonResult.fail("注册失败：邀请码无效");
        UserLoginPermissionPo userLoginPermissionPo = userBRoleMapper.selectBUserLoginInfoByStudentIdAndOrgId(data.getStudentId(), orgId);
        if (userLoginPermissionPo != null)
            return CommonResult.fail("加入组织失败：请勿重复加入组织");
        // 初始化一个新组织的新角色
        Role role = roleMapper.selectOne(new QueryWrapper<Role>().eq("organization_id", orgId).eq("permission_id", 3));
        if (role == null) {
            role = new Role();
            role.setOrganizationId(orgId);
            role.setPermissionId(3);
            int insert = roleMapper.insert(role);
            if (insert != 1) {
                log.warn("初始化组织 " + orgId + " 的角色时失败，受影响行数：" + insert);
                throw new DatabaseException("数据库错误");
            }
        }
        UserRoleMerge roleMerge = new UserRoleMerge();
        roleMerge.setUserId(data.getUserId());
        roleMerge.setRoleId(role.getId());
        roleMerge.setPassword(PasswordUtil.hashPassword(param.getPassword()));
        int insert = userRoleMergeMapper.insert(roleMerge);
        if (insert != 1) {
            log.warn("B 端用户 " + data + " 添加角色 " + roleMerge + "失败，受影响行数：" + insert);
            throw new DatabaseException("加入组织失败：数据库错误");
        }
        return CommonResult.success();
    }

    /**
     * B 端用户更新头像
     *
     * @param request  HTTP 请求报文
     * @param response HTTP 响应报文
     * @param avatar   头像文件
     * @return 处理结果，包含新头像的 URL
     * @author DoudiNCer
     */
    @Override
    public CommonResult<PutUserAvatarResult> putUserAvatar(HttpServletRequest request, HttpServletResponse response, MultipartFile avatar) {
        CommonResult<CheckRoleResult> check = checkRoleUtil.check(request, response);
        if (!Objects.equals(check.getCode(), ResultEnum.SUCCESS.getCode()))
            return CommonResult.fail(check.getCode(), check.getMessage());
        CheckRoleResult data = check.getData();
        UserB userB = userBMapper.selectById(data.getUserId());
        if (userB == null) {
            log.warn("获取 B 端用户信息失败：用户不存在，Token解析结果：" + data);
            return CommonResult.fail("数据库错误");
        }
        if (userB.getAvatarId() != null && userB.getAvatarId().length() != 0) {
            Boolean dropPicture = pictureUtil.dropPicture(userB.getAvatarId());
            if (dropPicture == null) {
                log.warn("B 端用户 " + data + " 更新头像删除原头像失败");
                return CommonResult.serverError();
            }
        }
        String pictureId = pictureUtil.uploadPicture(avatar,
                new PictureUsage(UsageEnum.B_USER_AVATAR, data.getToken()));
        if (pictureId == null) {
            log.warn("B 端用户 " + data + " 更新头像上传失败");
            return CommonResult.serverError();
        }
        userB.setAvatarId(pictureId);
        int updateById = userBMapper.updateById(userB);
        if (updateById != 1) {
            log.warn("B 端用户 " + data + " 更新头像更新数据库出现异常，受影响行数：" + updateById);
            return CommonResult.serverError();
        }
        PutUserAvatarResult result = new PutUserAvatarResult();
        result.setAvatarUrl(pictureUtil.getPictureURL(pictureId));
        return CommonResult.success(result);
    }
}

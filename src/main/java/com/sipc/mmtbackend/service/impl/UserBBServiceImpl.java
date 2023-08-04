package com.sipc.mmtbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sipc.mmtbackend.mapper.*;
import com.sipc.mmtbackend.pojo.domain.*;
import com.sipc.mmtbackend.pojo.domain.po.UserBRole.JoinedOrgPo;
import com.sipc.mmtbackend.pojo.domain.po.UserBRole.UserLoginPermissionPo;
import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.pojo.dto.param.UserBParam.*;
import com.sipc.mmtbackend.pojo.dto.result.UserBResult.*;
import com.sipc.mmtbackend.pojo.dto.result.UserBResult.po.JoinedOrgResultPo;
import com.sipc.mmtbackend.pojo.dto.result.UserBResult.po.LoginedJoinedOrgResultPo;
import com.sipc.mmtbackend.service.UserBService;
import com.sipc.mmtbackend.utils.CheckroleBUtil.JWTUtil;
import com.sipc.mmtbackend.utils.CheckroleBUtil.PasswordUtil;
import com.sipc.mmtbackend.utils.CheckroleBUtil.pojo.BTokenSwapPo;
import com.sipc.mmtbackend.utils.CheckroleBUtil.pojo.PermissionEnum;
import com.sipc.mmtbackend.utils.ICodeUtil;
import com.sipc.mmtbackend.utils.PictureUtil.PictureUtil;
import com.sipc.mmtbackend.utils.PictureUtil.pojo.DefaultPictureIdEnum;
import com.sipc.mmtbackend.utils.PictureUtil.pojo.PictureUsage;
import com.sipc.mmtbackend.utils.PictureUtil.pojo.UsageEnum;
import com.sipc.mmtbackend.utils.ThreadLocalContextUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.util.validation.metadata.DatabaseException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
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
    private final OrganizationMapper organizationMapper;
    private final PermissionMapper permissionMapper;
    private final FeedbackMapper feedbackMapper;
    private final ICodeUtil iCodeUtil;
    private final JWTUtil jwtUtil;
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
        Role role = roleMapper.selectOne(new QueryWrapper<Role>().eq("organization_id", orgId).eq("permission_id", PermissionEnum.NUMBER.getId()));
        if (role == null) {
            role = new Role();
            role.setOrganizationId(orgId);
            role.setPermissionId(PermissionEnum.COMMITTEE.getId());
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
            return CommonResult.fail("登录失败：用户名或密码错误");
        if (!PasswordUtil.testPasswd(param.getPassword(), userLoginPermission.getPassword()))
            return CommonResult.fail("登录失败：用户名或密码错误");
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
     * @return 用户信息
     * @author DoudiNCer
     */
    @Override
    public CommonResult<GetBUserInfoResult> getUserInfo() {
        BTokenSwapPo context = ThreadLocalContextUtil.getContext();
        GetBUserInfoResult result = new GetBUserInfoResult();
        UserB userB = userBMapper.selectById(context.getUserId());
        if (userB == null) {
            log.warn("获取 B 端用户信息失败：用户不存在，Token解析结果：" + context);
            return CommonResult.serverError();
        }
        Organization organization = organizationMapper.selectById(context.getOrganizationId());
        if (organization == null) {
            log.warn("获取 B 端用户信息失败：组织不存在，Token解析结果：" + context);
            return CommonResult.serverError();
        }
        Permission permission = permissionMapper.selectById(context.getPermissionId());
        if (permission == null) {
            log.warn("获取 B 端用户信息失败：权限不存在，Token解析结果：" + context);
            return CommonResult.serverError();
        }
        result.setUserId(userB.getId());
        result.setUsername(userB.getUserName());
        result.setStudentId(userB.getStudentId());
        result.setOrganizationId(organization.getId());
        result.setOrganizationName(organization.getName());
        result.setPermissionId(context.getPermissionId());
        result.setPermissionName(permission.getName());
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
            avatarUrl = pictureUtil.getPictureURL(DefaultPictureIdEnum.B_USER_AVATAR.getPictureId(), true);
        else
            avatarUrl = pictureUtil.getPictureURL(userB.getAvatarId(), false);
        result.setAvatarUrl(avatarUrl);
        return CommonResult.success(result);
    }

    /**
     * 更新 B 端用户密码
     *
     * @param param 旧密码与新密码
     * @return 处理结果
     * @author DoudiNCer
     */
    @Override
    public CommonResult<String> putUserNewPassword(PutUserPasswordParam param) {
        BTokenSwapPo context = ThreadLocalContextUtil.getContext();
        UserRoleMerge userRoleMerge = userBRoleMapper.selectUserRolleMergeByUserIdAndOrganizationIdAndPermissionId(
                context.getUserId(), context.getOrganizationId());
        if (userRoleMerge == null) {
            log.warn("获取 B 端用户信息失败：用户不存在，Token解析结果：" + context);
            return CommonResult.serverError();
        }
        boolean testPasswd = PasswordUtil.testPasswd(param.getOldPassword(), userRoleMerge.getPassword());
        if (!testPasswd) {
            return CommonResult.fail("更新密码失败：旧密码错误");
        }
        userRoleMerge.setPassword(PasswordUtil.hashPassword(param.getNewPassword()));
        int updateById = userRoleMergeMapper.updateById(userRoleMerge);
        if (updateById != 1) {
            log.warn("更新用户 " + userRoleMerge + " 密码错误，受影响行数：" + updateById);
            return CommonResult.serverError();
        }
        return CommonResult.success();
    }

    /**
     * B 端用户登出
     *
     * @return 处理结果
     */
    @Override
    public CommonResult<String> logout() {
        BTokenSwapPo context = ThreadLocalContextUtil.getContext();
        Boolean revokeToken = jwtUtil.revokeToken(context.getToken());
        if (revokeToken == null) {
            log.warn("用户 " + context + " 登出时出现异常");
            return CommonResult.serverError();
        }
        return CommonResult.success();
    }

    /**
     * B 端用户切换组织
     *
     * @param param 要切换的组织
     * @return 权限信息、新 Token
     */
    @Override
    public CommonResult<SwitchOrgResult> switchOrganization(SwitchOrgParam param) {
        BTokenSwapPo context = ThreadLocalContextUtil.getContext();
        UserLoginPermissionPo userLoginPermission = userBRoleMapper.selectBUserLoginInfoByUserIdAndOrgId(context.getUserId(), param.getOrganizationId());
        if (userLoginPermission == null) {
            log.info("用户 " + context + " 切换到组织 " + param + " 失败，组织不存在");
            return CommonResult.fail("切换组织失败，组织不存在或密码错误");
        }
        if (!PasswordUtil.testPasswd(param.getPassword(), userLoginPermission.getPassword())) {
            log.info("用户 " + context + " 切换到组织 " + param + " 失败，密码错误");
            return CommonResult.fail("切换组织失败，组织不存在或密码错误");
        }
        Boolean revokeToken = jwtUtil.revokeToken(context.getToken());
        if (revokeToken == null) {
            log.warn("用户 " + context + " 切换账号注销 Token 时出现异常");
            return CommonResult.serverError();
        }
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
     * @param param 邀请码与密码
     * @return 处理结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<String> addNewOrganization(AddNewOrgParam param) throws DatabaseException {
        BTokenSwapPo context = ThreadLocalContextUtil.getContext();
        if (context.getPermissionId() < PermissionEnum.NUMBER.getId())
            return CommonResult.fail("Super Admin 不允许加入其他组织");
        Integer orgId = iCodeUtil.verifyICode(param.getKey());
        if (orgId == null)
            return CommonResult.fail("注册失败：邀请码无效");
        UserB userB = userBMapper.selectById(context.getUserId());
        if (userB == null) {
            log.warn("用户 " + context + " 进入组织 " + param + " 失败，用户不存在");
            return CommonResult.serverError();
        }
        UserLoginPermissionPo userLoginPermissionPo = userBRoleMapper.selectBUserLoginInfoByUserIdAndOrgId(userB.getId(), orgId);
        if (userLoginPermissionPo != null)
            return CommonResult.fail("加入组织失败：请勿重复加入组织");
        // 初始化一个新组织的新角色
        Role role = roleMapper.selectOne(new QueryWrapper<Role>().eq("organization_id", orgId).eq("permission_id", PermissionEnum.COMMITTEE.getId()));
        if (role == null) {
            role = new Role();
            role.setOrganizationId(orgId);
            role.setPermissionId(PermissionEnum.COMMITTEE.getId());
            int insert = roleMapper.insert(role);
            if (insert != 1) {
                log.warn("初始化组织 " + orgId + " 的角色时失败，受影响行数：" + insert);
                throw new DatabaseException("数据库错误");
            }
        }
        UserRoleMerge roleMerge = new UserRoleMerge();
        roleMerge.setUserId(context.getUserId());
        roleMerge.setRoleId(role.getId());
        roleMerge.setPassword(PasswordUtil.hashPassword(param.getPassword()));
        int insert = userRoleMergeMapper.insert(roleMerge);
        if (insert != 1) {
            log.warn("B 端用户 " + context + " 添加角色 " + roleMerge + "失败，受影响行数：" + insert);
            throw new DatabaseException("加入组织失败：数据库错误");
        }
        return CommonResult.success();
    }

    /**
     * B 端用户更新头像
     *
     * @param avatar 头像文件
     * @return 处理结果，包含新头像的 URL
     * @author DoudiNCer
     */
    @Override
    public CommonResult<PutUserAvatarResult> putUserAvatar(MultipartFile avatar) {
        BTokenSwapPo context = ThreadLocalContextUtil.getContext();
        UserB userB = userBMapper.selectById(context.getUserId());
        if (userB == null) {
            log.warn("获取 B 端用户信息失败：用户不存在，Token解析结果：" + context);
            return CommonResult.serverError();
        }
        if (userB.getAvatarId() != null && userB.getAvatarId().length() != 0) {
            Boolean dropPicture = pictureUtil.dropPicture(userB.getAvatarId());
            if (dropPicture == null) {
                log.warn("B 端用户 " + context + " 更新头像时删除原头像失败");
                return CommonResult.serverError();
            }
        }
        String pictureId = pictureUtil.uploadPicture(avatar,
                new PictureUsage(UsageEnum.B_USER_AVATAR, context.getToken()));
        if (pictureId == null) {
            log.warn("B 端用户 " + context + " 更新头像上传失败");
            return CommonResult.serverError();
        }
        userB.setAvatarId(pictureId);
        int updateById = userBMapper.updateById(userB);
        if (updateById != 1) {
            log.warn("B 端用户 " + context + " 更新头像更新数据库出现异常，受影响行数：" + updateById);
            return CommonResult.serverError();
        }
        PutUserAvatarResult result = new PutUserAvatarResult();
        result.setAvatarUrl(pictureUtil.getPictureURL(pictureId, false));
        return CommonResult.success(result);
    }

    /**
     * 登录后获取已加入的组织及当前登录的组织
     *
     * @return 用户已加入的组织及是否为当前组织
     */
    @Override
    public CommonResult<LoginedJoinOrgsResult> getLoginedJoinedOrgs() {
        BTokenSwapPo context = ThreadLocalContextUtil.getContext();
        List<LoginedJoinedOrgResultPo> results = new LinkedList<>();
        for (JoinedOrgPo joinedOrgPo : userBRoleMapper.selectJoinedOrganizationsByBUserId(context.getUserId())) {
            LoginedJoinedOrgResultPo po = new LoginedJoinedOrgResultPo();
            po.setOrganizationId(joinedOrgPo.getOrganizationId());
            po.setOrganizationName(joinedOrgPo.getOrganizationName());
            po.setActive(Objects.equals(context.getOrganizationId(), joinedOrgPo.getOrganizationId()));
            results.add(po);
        }
        LoginedJoinOrgsResult result = new LoginedJoinOrgsResult();
        result.setNum(results.size());
        result.setOrganizations(results);
        return CommonResult.success(result);
    }

    /**
     * B 端反馈
     *
     * @param param 用户名、邮箱与反馈信息
     * @return 反馈结果
     */
    @Override
    public CommonResult<String> feedback(BFeedbackParam param) {
        BTokenSwapPo context = ThreadLocalContextUtil.getContext();
        Feedback feedback = new Feedback();
        feedback.setDate(LocalDateTime.now());
        feedback.setEmail(param.getEmail());
        feedback.setFeedback(param.getFeedback());
        feedback.setIsB(true);
        feedback.setName(param.getName());
        feedback.setUserId(context.getUserId());
        int insert = feedbackMapper.insert(feedback);
        if (insert != 1){
            log.warn("用户 " + context + " 反馈问题 " + feedback + "时出现异常");
            return CommonResult.serverError();
        }
        return CommonResult.success();
    }
}

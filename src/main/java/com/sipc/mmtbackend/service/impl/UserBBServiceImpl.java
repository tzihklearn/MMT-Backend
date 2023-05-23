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
import com.sipc.mmtbackend.pojo.dto.param.UserBParam.LoginPassParam;
import com.sipc.mmtbackend.pojo.dto.param.UserBParam.PutUserPasswordParam;
import com.sipc.mmtbackend.pojo.dto.param.UserBParam.RegParam;
import com.sipc.mmtbackend.pojo.dto.param.UserBParam.SwitchOrgParam;
import com.sipc.mmtbackend.pojo.dto.result.UserBResult.GetBUserInfoResult;
import com.sipc.mmtbackend.pojo.dto.result.UserBResult.JoinOrgsResult;
import com.sipc.mmtbackend.pojo.dto.result.UserBResult.LoginResult;
import com.sipc.mmtbackend.pojo.dto.result.UserBResult.SwitchOrgResult;
import com.sipc.mmtbackend.pojo.dto.result.UserBResult.po.JoinedOrgResultPo;
import com.sipc.mmtbackend.pojo.dto.resultEnum.ResultEnum;
import com.sipc.mmtbackend.service.UserBService;
import com.sipc.mmtbackend.utils.CheckroleBUtil.CheckRoleUtil;
import com.sipc.mmtbackend.utils.CheckroleBUtil.JWTUtil;
import com.sipc.mmtbackend.utils.CheckroleBUtil.PasswordUtil;
import com.sipc.mmtbackend.utils.CheckroleBUtil.pojo.BTokenSwapPo;
import com.sipc.mmtbackend.utils.CheckroleBUtil.pojo.CheckRoleResult;
import com.sipc.mmtbackend.utils.ICodeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.util.validation.metadata.DatabaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            throw new DatabaseException("创建用户失败");
        }
        roleMerge.setRoleId(role.getId());
        roleMerge.setUserId(user.getId());
        roleMerge.setPassword(PasswordUtil.hashPassword(param.getPassword()));
        int urint = userRoleMergeMapper.insert(roleMerge);
        if (urint != 1) {
            log.warn("创建 B 端用户 " + user + " 的角色" + roleMerge + "失败，受影响行数：" + urint);
            throw new DatabaseException("创建用户失败");
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
        return CommonResult.success(token);
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
        StringBuilder sb = new StringBuilder();
        String phone = userB.getPhone();
        sb.append(phone, 0, 3);
        sb.append(" **** ");
        sb.append(phone.substring(7));
        result.setPhone(sb.toString());
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
}

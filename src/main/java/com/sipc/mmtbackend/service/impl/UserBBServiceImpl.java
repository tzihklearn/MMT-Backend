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
import com.sipc.mmtbackend.pojo.dto.param.UserBParam.RegParam;
import com.sipc.mmtbackend.pojo.dto.result.UserBResult.JoinOrgsResult;
import com.sipc.mmtbackend.pojo.dto.result.UserBResult.LoginResult;
import com.sipc.mmtbackend.pojo.dto.result.UserBResult.po.JoinedOrgResultPo;
import com.sipc.mmtbackend.service.UserBService;
import com.sipc.mmtbackend.utils.CheckroleBUtil.JWTUtil;
import com.sipc.mmtbackend.utils.CheckroleBUtil.PasswordUtil;
import com.sipc.mmtbackend.utils.CheckroleBUtil.po.BTokenSwapPo;
import com.sipc.mmtbackend.utils.ICodeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.util.validation.metadata.DatabaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<String> registUser(RegParam param) throws DatabaseException {
        Integer orgId = iCodeUtil.verifyICode(param.getKey());
        // 科协测试邀请码
        if (Objects.equals(param.getKey(), "qwertyuiop"))
                orgId = 1;
        if (orgId == null)
            return CommonResult.fail("邀请码无效");
        // 初始化一个新组织的新角色
        Role role = roleMapper.selectOne(new QueryWrapper<Role>().eq("organization_id", orgId).eq("permission_id", 3));
        if (role == null){
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
        if (uins != 1){
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
     * @param studentId 用户账号（学号）
     * @return 加入的组织列表
     */
    @Override
    public CommonResult<JoinOrgsResult> getJoinedOrgs(String studentId) {
        UserB userB = userBMapper.selectOne(new QueryWrapper<UserB>().eq("studen_id", studentId));
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
}

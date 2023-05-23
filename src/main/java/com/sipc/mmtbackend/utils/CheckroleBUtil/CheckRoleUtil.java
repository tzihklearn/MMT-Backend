package com.sipc.mmtbackend.utils.CheckroleBUtil;

import com.sipc.mmtbackend.mapper.OrganizationMapper;
import com.sipc.mmtbackend.mapper.PermissionMapper;
import com.sipc.mmtbackend.mapper.RoleMapper;
import com.sipc.mmtbackend.mapper.UserBMapper;
import com.sipc.mmtbackend.pojo.domain.Organization;
import com.sipc.mmtbackend.pojo.domain.Permission;
import com.sipc.mmtbackend.pojo.domain.Role;
import com.sipc.mmtbackend.pojo.domain.UserB;
import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.pojo.dto.resultEnum.ResultEnum;
import com.sipc.mmtbackend.utils.CheckroleBUtil.pojo.BTokenSwapPo;
import com.sipc.mmtbackend.utils.CheckroleBUtil.pojo.CheckRoleResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 鉴权相关工具
 *
 * @author DoudiNCer
 */
@Component
@Slf4j
public class CheckRoleUtil {

    private final JWTUtil jwtUtil;
    private final RoleMapper roleMapper;
    private final OrganizationMapper organizationMapper;
    private final PermissionMapper permissionMapper;
    private final UserBMapper userBMapper;
    private final Map<String, Integer> apiPermissions;

    @Autowired
    public CheckRoleUtil(JWTUtil jwtUtil, RoleMapper roleMapper, OrganizationMapper organizationMapper, PermissionMapper permissionMapper, UserBMapper userBMapper) {
        this.jwtUtil = jwtUtil;
        this.roleMapper = roleMapper;
        this.organizationMapper = organizationMapper;
        this.permissionMapper = permissionMapper;
        this.userBMapper = userBMapper;
        Map<String, Integer> apiPermissions = new HashMap<>();
        this.apiPermissions = apiPermissions;
    }

    /**
     * Token 鉴权获取用户信息
     *
     * @param token JWT Token
     * @return 用户数据与查询结果
     * @author DoudiNCer
     */
    private CommonResult<CheckRoleResult> checkUsderInfoByToken(String token) {
        BTokenSwapPo bTokenSwapPo = jwtUtil.verifyToken(token);
        if (bTokenSwapPo == null) {
            log.info("鉴权验证Token失败：" + token);
            return CommonResult.fail("Token 验证失败");
        }
        Role role = roleMapper.selectById(bTokenSwapPo.getRoleId());
        if (role == null) {
            log.warn("鉴权查无角色代码，Token：" + token + "，载荷：" + bTokenSwapPo);
            return CommonResult.fail("鉴权查无角色信息");
        }
        Organization organization = organizationMapper.selectById(role.getOrganizationId());
        if (organization == null) {
            log.warn("鉴权角色信息错误，查无组织：" + role);
            return CommonResult.fail("鉴权角色信息错误");
        }
        Permission permission = permissionMapper.selectById(role.getPermissionId());
        if (permission == null) {
            log.warn("鉴权角色信息错误，查无权限：" + role);
            return CommonResult.fail("鉴权角色信息错误");
        }
        UserB userB = userBMapper.selectById(bTokenSwapPo.getUserId());
        if (userB == null) {
            log.warn("鉴权查无用户信息：" + bTokenSwapPo);
            return CommonResult.fail("Token 数据错误");
        }
        CheckRoleResult result = new CheckRoleResult();
        result.setUserId(bTokenSwapPo.getUserId());
        result.setStudentId(bTokenSwapPo.getStudentId());
        result.setUsername(userB.getUserName());
        result.setOrganizationId(role.getOrganizationId());
        result.setUsername(organization.getName());
        result.setPermissionId(role.getPermissionId());
        result.setPermissionName(permission.getName());
        result.setRoleId(bTokenSwapPo.getRoleId());
        return CommonResult.success(result);
    }

    /**
     * 校验 Token 并检查其权限
     *
     * @param req HttpServletRequest
     * @return 检查结果，CommonResult&lt;CheckRoleResult&gt;，检查失败时可直接返回
     * @author DoudiNCer
     */
    public CommonResult<CheckRoleResult> check(HttpServletRequest req, HttpServletResponse resp) {
        String token = req.getHeader("Authorization");
        if (token == null) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return CommonResult.fail("无Token");
        }
        CommonResult<CheckRoleResult> checkRoleResult = checkUsderInfoByToken(token);
        if (!Objects.equals(checkRoleResult.getCode(), ResultEnum.SUCCESS.getCode())) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return checkRoleResult;
        }
        String requestURI = req.getRequestURI();
        Integer apiPermission = apiPermissions.get(requestURI);
        CheckRoleResult userdata = checkRoleResult.getData();
        if (userdata.getPermissionId() > apiPermission) {
            log.info("用户 " + userdata + " 尝试越权访问 " + requestURI + "（权限为" + apiPermission +"）");
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return CommonResult.fail("权限不足");
        }
        return checkRoleResult;
    }
}

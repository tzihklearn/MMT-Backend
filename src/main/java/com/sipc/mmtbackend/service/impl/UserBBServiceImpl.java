package com.sipc.mmtbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sipc.mmtbackend.mapper.UserBMapper;
import com.sipc.mmtbackend.mapper.UserRoleMapper;
import com.sipc.mmtbackend.pojo.domain.UserB;
import com.sipc.mmtbackend.pojo.domain.po.UserRolePermissionPo;
import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.pojo.dto.param.UserBParam.LoginPassParam;
import com.sipc.mmtbackend.pojo.dto.param.UserBParam.RegParam;
import com.sipc.mmtbackend.pojo.dto.result.UserBResult.LoginResult;
import com.sipc.mmtbackend.pojo.dto.result.UserBResult.po.UserPermissionPo;
import com.sipc.mmtbackend.pojo.exceptions.DateBaseException;
import com.sipc.mmtbackend.service.UserBService;
import com.sipc.mmtbackend.utils.CheckroleBUtil.JWTUtil;
import com.sipc.mmtbackend.utils.CheckroleBUtil.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Slf4j
public class UserBBServiceImpl implements UserBService {
    private UserBMapper userBMapper;

    private JWTUtil jwtUtil;

    private UserRoleMapper userRoleMapper;

    @Override
    public CommonResult<String> registUser(RegParam param) throws DateBaseException {
        UserB user = new UserB();
        user.setPhone(param.getPhoneNum());
        user.setStudentId(param.getStudentId());
        user.setPassword(PasswordUtil.hashPassword(param.getPassword()));
        user.setUserName(param.getUsername());
        user.setEmail(param.getEmail());
        int uins = userBMapper.insert(user);
        if (uins != 1){
            log.error("创建B端用户失败：" + user);
            throw new DateBaseException("系统错误，创建用户失败");
        }
        return CommonResult.success();
    }

    @Override
    public CommonResult<LoginResult> loginByPass(LoginPassParam param) {
        UserB userB = userBMapper.selectOne(new QueryWrapper<UserB>().eq("student_id", param.getStudentId()));
        if (userB == null)
            return CommonResult.fail("用户名或密码错误");
        if (!PasswordUtil.testPasswd(param.getPassword(), userB.getPassword()))
            return CommonResult.fail("用户名或密码错误");
        String token = jwtUtil.createToken(userB);
        LoginResult result = new LoginResult();
        result.setUserId(userB.getId());
        result.setToken(token);
        result.setUsername(userB.getUserName());
        List<UserPermissionPo> permissions = new LinkedList<>();
        for (UserRolePermissionPo permissionPo : userRoleMapper.selectAllRolePermissionByUserId(userB.getId())) {
            UserPermissionPo po = new UserPermissionPo();
            po.setPermissionId(permissionPo.getPermissionId());
            po.setPermissionName(permissionPo.getPermissionName());
            po.setOrganizationId(permissionPo.getOrganizationId());
            permissions.add(po);
        }
        result.setPermissions(permissions);
        return CommonResult.success(token);
    }
}

package com.sipc.mmtbackend.mapper;

import com.sipc.mmtbackend.pojo.domain.po.UserBRole.JoinedOrgPo;
import com.sipc.mmtbackend.pojo.domain.po.UserBRole.UserLoginPermissionPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 自定义的 B 端鉴权相关 Mapper
 *
 * @author DoudiNCer
 */
@Mapper
public interface UserBRoleMapper {
    /**
     * 根据 B 端用户 ID 查询所有加入的组织
     *
     * @param id B 端用户 ID
     * @return 用户加入的组织（组织 ID 与组织名称）
     */
    List<JoinedOrgPo> selectJoinedOrganizationsByBUserId(Integer id);

    /**
     * 根据 B 端用户账号（学号）与组织 ID 查询用户在该组织的登录信息
     *
     * @param studentId      B 端用户账号（学号）
     * @param organizationId 组织ID
     * @return UserLoginPermissionPo 登录信息，包括用户 ID、用户名、密码、角色 ID、 角色名
     */
    UserLoginPermissionPo selectBUserLoginInfoByStudentIdAndOrgId(@Param("studentId") String studentId, @Param("organizationId") Integer organizationId);
}

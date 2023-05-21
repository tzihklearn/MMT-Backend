package com.sipc.mmtbackend.mapper;

import com.sipc.mmtbackend.pojo.domain.po.UserBRole.JoinedOrgPo;
import com.sipc.mmtbackend.pojo.domain.po.UserBRole.UserLoginPermissionPo;
import com.sipc.mmtbackend.pojo.domain.po.UserBRole.UserRolePermissionPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserBRoleMapper {

    List<UserRolePermissionPo> selectAllRolePermissionByBUserId(Integer id);
    List<JoinedOrgPo> selectJoinedOrganizationsByBUserId(Integer id);
    UserLoginPermissionPo selectBUserLoginInfoByStudentIdAndOrgId(@Param("studentId") String studentId, @Param("organizationId") Integer organizationId);
}

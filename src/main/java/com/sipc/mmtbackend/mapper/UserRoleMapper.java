package com.sipc.mmtbackend.mapper;

import com.sipc.mmtbackend.pojo.domain.po.UserRolePermissionPo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserRoleMapper{

    List<UserRolePermissionPo> selectAllRolePermissionByUserId(Integer id);
}

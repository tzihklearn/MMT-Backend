package com.sipc.mmtbackend.mapper;

import com.sipc.mmtbackend.pojo.domain.UserRoleMerge;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sipc.mmtbackend.pojo.domain.po.UserRolePermissionPo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author DoudiNCer
 * @since 2023-04-27
 */
@Mapper
public interface UserRoleMergeMapper extends BaseMapper<UserRoleMerge> {
    List<UserRolePermissionPo> selectAllRolePermissionByUserId(Integer userId);
}

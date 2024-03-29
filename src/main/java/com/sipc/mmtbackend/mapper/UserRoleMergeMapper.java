package com.sipc.mmtbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sipc.mmtbackend.pojo.domain.UserRoleMerge;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author DoudiNCer
 * @since 2023-05-21
 */
@Mapper
public interface UserRoleMergeMapper extends BaseMapper<UserRoleMerge> {
    int updateRoleIdByUserIdAndOrganizationId(Integer userId, Integer organizationId, Integer roleId);

    int updatePasswdByUserIdAndOrganizationId(Integer userId, Integer organizationId, String passwd);

    int updateRoleByUserIdAndOrganizationId(Integer userId, Integer organizationId, Integer roleId, String passwd);

    int logicDeleteByUserIdAndOrganizationId(Integer userId, Integer organizationId);
}

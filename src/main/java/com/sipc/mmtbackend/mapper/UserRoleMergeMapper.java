package com.sipc.mmtbackend.mapper;

import com.sipc.mmtbackend.pojo.domain.UserRoleMerge;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author DoudiNCer
 * @since 2023-05-21
 */
@Mapper
public interface UserRoleMergeMapper extends BaseMapper<UserRoleMerge> {
    int updateRoleIdByUserIdAndOrganizationId(Integer userId, Integer organizationId, Integer roleId);
}

package com.sipc.mmtbackend.mapper.c;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CheckInMapper {
    /**
     * 签到
     *
     * @param uid C 端用户 ID
     * @param organizationId 组织ID
     * @return 修改条数
     */
    int checkInUser(@Param("userId") Integer uid, @Param("organizationId") Integer organizationId);
}

package com.sipc.mmtbackend.mapper;

import com.sipc.mmtbackend.pojo.domain.UserB;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sipc.mmtbackend.pojo.domain.po.UserBMemberPo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author DoudiNCer
 * @since 2023-05-21
 */
@Mapper
public interface UserBMapper extends BaseMapper<UserB> {
    List<UserBMemberPo> selectMemberInfoListByOrganizationId(Integer organizationId, Integer startNum, Integer endNum,
                                                             Integer sort, Integer permissionId);
}

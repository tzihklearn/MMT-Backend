package com.sipc.mmtbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sipc.mmtbackend.pojo.domain.OrganizationDepartmentMerge;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author DoudiNCer
 * @since 2023-05-06
 */
@Mapper
public interface OrganizationDepartmentMergeMapper extends BaseMapper<OrganizationDepartmentMerge> {

    List<Integer> selectDepartmentIdByAdmissionId(Integer organizationId);

}

package com.sipc.mmtbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sipc.mmtbackend.pojo.c.result.DepartmentResult;
import com.sipc.mmtbackend.pojo.domain.Department;
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
public interface DepartmentMapper extends BaseMapper<Department> {

    Integer selectOrganizationIdById(Integer departmentId);

    List<DepartmentResult> selectRelationByAdmissionId(Integer admissionId);

}

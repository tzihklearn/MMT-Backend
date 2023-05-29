package com.sipc.mmtbackend.mapper;

import com.sipc.mmtbackend.pojo.domain.po.InterviewBoardPo.PersonNumGroupByDepartmentPo;
import com.sipc.mmtbackend.pojo.domain.po.InterviewBoardPo.TotalNumPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 面试看板数据查询 Mapper
 *
 * @author DoudiNCer
 */
@Mapper
public interface InterviewBoardDataMapper {
    List<PersonNumGroupByDepartmentPo> selectSignInPersonNumberGroupByDepartmentByOrganizationIdAndAdmissionId(
            @Param("organizationId") Integer organizationId,
            @Param("admissionId") Integer admissionId);
    TotalNumPo selectTotalNumByDepartmentIdAndAdmissionId(
            @Param("departmentId") Integer departmentId,
            @Param("admissionId") Integer admissionId);
}

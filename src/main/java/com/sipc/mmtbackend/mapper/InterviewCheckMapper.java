package com.sipc.mmtbackend.mapper;

import com.sipc.mmtbackend.pojo.domain.Admission;
import com.sipc.mmtbackend.pojo.domain.AdmissionAddress;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用于各类复杂的检查、选项范围的获取
 *
 * @author DoudiNCer
 */
@Mapper
public interface InterviewCheckMapper {

    /**
     * 查询组织活动的面试
     *
     * @param organizationId 组织ID
     * @return 正在进行的面试
     */
    Admission selectOrganizationActivateAdmission(
            @Param("organizationId") Integer organizationId
    );

    /**
     * 查询组织已开始的面试轮次
     *
     * @param admissionId 纳新ID
     * @return 该纳新下已开始的最大面试轮次
     */
    Integer selectOrganizationActivateInterviewRound(
            @Param("admissionId") Integer admissionId
    );

    /**
     * 查询已分配的面试场地
     *
     * @param round 面试轮次
     * @param admissionId 纳新ID
     * @param DepartmentId 部门ID
     * @return 面试地点
     */
    List<AdmissionAddress> selectAvailableInterviewAddress(
            @Param("round") Integer round,
            @Param("admissionId") Integer admissionId,
            @Param("departmentId") Integer DepartmentId
    );

    /**
     * 查询指定的面试地点是否在当前部门的当前面试中使用
     *
     * @param round 面试轮次
     * @param admissionId 纳新ID
     * @param DepartmentId 部门ID
     * @return 面试场地信息或 null
     */
    AdmissionAddress selectAddressByIdAndNowData(
            @Param("addressId") Integer addressId,
            @Param("round") Integer round,
            @Param("admissionId") Integer admissionId,
            @Param("departmentId") Integer DepartmentId
    );
}

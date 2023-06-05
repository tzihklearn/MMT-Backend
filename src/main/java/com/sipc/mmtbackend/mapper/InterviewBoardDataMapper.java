package com.sipc.mmtbackend.mapper;

import com.sipc.mmtbackend.pojo.domain.po.InterviewBoardPo.LineChartLineDataDaoPo;
import com.sipc.mmtbackend.pojo.domain.po.InterviewBoardPo.PersonNumGroupByDepartmentPo;
import com.sipc.mmtbackend.pojo.domain.po.InterviewBoardPo.PersonNumGroupByOrderPo;
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
    /**
     * 根据纳新 ID 查询各个部门总报名人数与第一志愿报名人数
     *
     * @param admissionId 纳新 ID
     * @return 各个部门报名人数
     */
    List<PersonNumGroupByDepartmentPo> selectSignInPersonNumberGroupByDepartmentByAndAdmissionId(
            @Param("admissionId") Integer admissionId);

    /**
     * 根据部门 ID 与纳新 ID 查询该部门报名人数与第一志愿人数
     *
     * @param departmentId 部门 ID
     * @param admissionId  纳新 ID
     * @return 报名人数
     */
    TotalNumPo selectTotalNumByDepartmentIdAndAdmissionId(
            @Param("departmentId") Integer departmentId,
            @Param("admissionId") Integer admissionId);

    /**
     * 根据组织 ID 与纳新 ID 查询各个部门报名人数折线图数据
     *
     * @param organizationId 组织 ID
     * @param admissionId    纳新 ID
     * @return 折线图数据
     */
    List<LineChartLineDataDaoPo> selectInterviewNumberLineChartGroupByDepartmentDataByOrganizationIdAndAdmissionId(
            @Param("organizationId") Integer organizationId,
            @Param("admissionId") Integer admissionId);

    /**
     * 根据纳新 ID 与 部门 ID 查询部门各个志愿人数
     *
     * @param admissionId  纳新 ID
     * @param departmentId 部门ID
     * @return 志愿编号与人数
     */
    List<PersonNumGroupByOrderPo> selectNumberGroupByOrderByAdmissionIdAndDepartmentId(
            @Param("admissionId") Integer admissionId,
            @Param("departmentId") Integer departmentId);
}

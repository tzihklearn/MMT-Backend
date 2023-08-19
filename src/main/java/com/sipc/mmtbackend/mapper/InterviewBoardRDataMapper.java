package com.sipc.mmtbackend.mapper;

import com.sipc.mmtbackend.pojo.domain.po.InterviewBoardRPo.DepartmentPassedCountPo;
import com.sipc.mmtbackend.pojo.domain.po.InterviewBoardRPo.InterviewResultData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 面试看板（面试后）数据查询 Mapper
 *
 * @author DoudiNCer
 */
@Mapper
public interface InterviewBoardRDataMapper {

    /**
     * 查询面试最终数据
     *
     * @param round 面试轮次
     * @param admissionId 纳新ID
     * @param departmentId 部门ID
     * @return 面试最终数据
     */
    InterviewResultData selectInterviewResultData(
            @Param("round") Integer round,
            @Param("admissionId") Integer admissionId,
            @Param("departmentId") Integer departmentId
    );

    /**
     * 查询各个部门通过i人数
     *
     * @param round 面试轮次
     * @param admissionId 纳新ID
     * @return 各个部门通过人数
     */
    List<DepartmentPassedCountPo> selectPassedCountPerDepartment(
            @Param("round") Integer round,
            @Param("admissionId") Integer admissionId
    );
}

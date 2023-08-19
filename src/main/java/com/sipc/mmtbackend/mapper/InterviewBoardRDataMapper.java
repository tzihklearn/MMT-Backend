package com.sipc.mmtbackend.mapper;

import com.sipc.mmtbackend.pojo.domain.po.InterviewBoardRPo.InterviewResultData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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
}

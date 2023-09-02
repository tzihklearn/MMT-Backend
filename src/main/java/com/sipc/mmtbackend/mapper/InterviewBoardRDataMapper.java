package com.sipc.mmtbackend.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sipc.mmtbackend.pojo.domain.po.InterviewBoardRPo.InterviewScoreAndRankPo;
import com.sipc.mmtbackend.pojo.domain.po.InterviewBoardRPo.DepartmentPassedCountPo;
import com.sipc.mmtbackend.pojo.domain.po.InterviewBoardRPo.InterviewResultData;
import com.sipc.mmtbackend.pojo.domain.po.InterviewBoardRPo.LineChartLineDataDaoPo;
import com.sipc.mmtbackend.pojo.domain.po.InterviewBoardRPo.OrderPassedCountPo;
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
     * 查询各个部门通过人数
     *
     * @param round 面试轮次
     * @param admissionId 纳新ID
     * @return 各个部门通过人数
     */
    List<DepartmentPassedCountPo> selectPassedCountPerDepartment(
            @Param("round") Integer round,
            @Param("admissionId") Integer admissionId
    );

    /**
     * 查询不同部门通过人数随面试轮次变化情况
     *
     * @param admissionId 纳新ID
     * @return 折线图数据
     */
    List<LineChartLineDataDaoPo> selectPassedCountLineChartGroupByRoundByAdmissionId(
            @Param("admissionId") Integer admissionId);

    /**
     * 查询最终各个志愿通过人数
     *
     * @param admissionId 纳新ID
     * @param departmentId 部门ID
     * @param round 年事轮次
     * @return 最终各个志愿通过人数
     */
    List<OrderPassedCountPo> selectPassedCountPerOrder(
            @Param("round") Integer round,
            @Param("admissionId") Integer admissionId,
            @Param("departmentId") Integer departmentId
    );

    /**
     * 查询不同志愿通过人数随面试轮次变化情况
     *
     * @param admissionId 纳新ID
     * @param departmentId 部门ID
     * @return 折线图数据
     */
    List<LineChartLineDataDaoPo> selectPassedCountLineChartGroupByRoundAndOrderByAdmissionId(
            @Param("admissionId") Integer admissionId,
            @Param("departmentId") Integer departmentId
    );

    /**
     * 查询通过面试的人员面试分数与排名
     *
     * @param page MyBatis Plus 分页器
     * @param round 面试轮次
     * @param admissionId 纳新ID
     * @param departmentId 部门ID
     * @return 面试分数与排名
     */
    IPage<InterviewScoreAndRankPo> selectPassedInterviewScoreAndRank(
            Page<InterviewScoreAndRankPo> page,
            @Param("round") Integer round,
            @Param("admissionId") Integer admissionId,
            @Param("departmentId") Integer departmentId
    );
}

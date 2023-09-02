package com.sipc.mmtbackend.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sipc.mmtbackend.pojo.domain.po.RealtimeInterviewPo.InterviewEvaluationAndAnswerPo;
import com.sipc.mmtbackend.pojo.domain.po.RealtimeInterviewPo.InterviewStatusPo;
import com.sipc.mmtbackend.pojo.domain.po.RealtimeInterviewPo.ProgressBarPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用于实时面试的自定义 Mapper
 *
 * @author DoudiNCer
 */
@Mapper
public interface RealtimeInterviewMapper {
    /**
     * 查询实时面试进度
     *
     * @param round 面试轮次
     * @param admissionId 纳新ID
     * @param placeId 面试场地
     * @return 各时间段进度
     */
    List<ProgressBarPo> selectInterviewProgress(
            @Param("round") Integer round,
            @Param("admissionId") Integer admissionId,
            @Param("place") Integer placeId
    );

    /**
     * 获取今日实时面试数据
     *
     * @param page MyBatis Plus 分页器
     * @param keyword 搜索关键词
     * @param round 面试轮次（理论上不需要）
     * @param admissionId 纳新ID
     * @param placeId 面试地点
     * @return 今日分页后的面熟数据
     */
    IPage<InterviewStatusPo> selectRealtimeInterviewData(
            IPage<InterviewStatusPo> page,
            @Param("keyword") String keyword,
            @Param("round") Integer round,
            @Param("admissionId") Integer admissionId,
            @Param("place") Integer placeId
    );

    /**
     * 根据 B、C 端用户 ID 查询所有面试评价问题与回答
     *
     * @param admissionId 纳新ID
     * @param bId B 端用户 ID
     * @param cId C 端用户 ID
     * @return 所有面试评价问题与回答
     */
    List<InterviewEvaluationAndAnswerPo> selectAllInterviewEvaluationQnAByBCUID(
            @Param("admissionId") Integer admissionId,
            @Param("bId") Integer bId,
            @Param("cId") Integer cId
    );

    /**
     * 查询指定面试的面试问卷与
     *
     * @param interviewStatusId 面试状态ID
     * @param bId 面试者ID
     * @return 面试问题与回答
     */
    InterviewEvaluationAndAnswerPo selectlatestInterviewEvaluationQnA(
            @Param("iId") Integer interviewStatusId,
            @Param("bId") Integer bId
    );
}

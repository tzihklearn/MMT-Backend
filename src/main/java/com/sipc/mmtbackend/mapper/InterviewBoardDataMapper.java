package com.sipc.mmtbackend.mapper;

import com.sipc.mmtbackend.pojo.domain.InterviewStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface InterviewBoardDataMapper {
    /**
     * 查询已签到人数
     *
     * @param admissionId 纳新ID
     * @return 已签到人数
     */
    Integer selectCountOfCheckinInterview(
            @Param("admissionId") Integer admissionId
    );

    /**
     * 查询最后一轮未结束的面试
     *
     * @param admissionId 纳新ID
     * @param round 轮次
     * @return 未结束的面试
     */
    List<InterviewStatus> selectNotFinishedInterview(
            @Param("admissionId") Integer admissionId,
            @Param("round") Integer round
    );
}

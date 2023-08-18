package com.sipc.mmtbackend.mapper;

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
}

package com.sipc.mmtbackend.mapper;

import com.sipc.mmtbackend.pojo.domain.po.InterviewBoardMPo.CheckinInfoLPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 面试看板（面试中）数据查询 Mapper
 *
 * @author DoudiNCer
 */
@Mapper
public interface InterviewBoardMDataMapper {
    /**
     * 重新签到信息
     *
     * @param round 面试轮次
     * @param admissionId 纳新ID
     * @param DepartmentId 部门ID
     * @return 签到信息（学号、姓名、面试地点、签到时间）
     */
    List<CheckinInfoLPo> selectCheckinInfo(
            @Param("round") Integer round,
            @Param("admissionId") Integer admissionId,
            @Param("departmentId") Integer DepartmentId
    );
}

package com.sipc.mmtbackend.pojo.dto.result.RealtimeIntreviewdResult;

import com.sipc.mmtbackend.pojo.dto.result.RealtimeIntreviewdResult.po.IntervieweePo;
import lombok.Data;

import java.util.List;

@Data
public class GetIntervieweeListResult {
    // 总页数
    private Integer pages;
    // 本页数据量
    private Integer count;
    // 面试人员信息
    private List<IntervieweePo> interviewees;
}

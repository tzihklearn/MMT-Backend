package com.sipc.mmtbackend.pojo.dto.result.RealtimeIntreviewdResult;

import com.sipc.mmtbackend.pojo.dto.result.RealtimeIntreviewdResult.po.InterviewTablePo;
import lombok.Data;

import java.util.List;

@Data
public class GetInterviewCommentResult {
    // 数量
    private Integer count;
    // 面试官名字
    private String interviewer;
    // 面试部门
    private String department;
    // 面试表数据
    private List<InterviewTablePo> interviewTables;
}

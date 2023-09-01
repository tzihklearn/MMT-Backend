package com.sipc.mmtbackend.pojo.domain.po.RealtimeInterviewPo;

import lombok.Data;

import java.util.List;

@Data
public class InterviewEvaluationAndAnswerPo {
    // 面试轮次
    private Integer round;
    // 是否实名评价
    private Boolean realName;
    // 面试官期待的部门
    private Integer expectDepartment;
    // 问题与答案
    private List<InterviewEvaluationQAPo> questions;
}

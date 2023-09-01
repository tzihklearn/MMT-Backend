package com.sipc.mmtbackend.pojo.domain.po.RealtimeInterviewPo;

import lombok.Data;

import java.util.List;

@Data
public class InterviewEvaluationAndAnswerPo {
    // 面试轮次
    private Integer round;
    // 是否实名评价
    private Boolean realName;
    // 是否通过（1 通过，2失败，3待定）
    private Integer isPass;
    // 面试官期待的部门
    private Integer expectDepartment;
    // 问题与答案
    private List<InterviewEvaluationQAPo> questions;
}

package com.sipc.mmtbackend.pojo.dto.param.RealtimeInterview;

import com.sipc.mmtbackend.pojo.dto.param.RealtimeInterview.po.InterviewEvaluationPo;
import lombok.Data;

import java.util.List;

@Data
public class PostInterviewCommentParam {
    // 面试ID
    private Integer interview;
    // 是否实名评价
    private Boolean realName;
    // 是否通过（1 通过，2失败，3待定）
    private Integer isPass;
    // 面试官期待的部门
    private Integer expectDepartment;
    // 面试问题的回答
    private List<InterviewEvaluationPo> evaluations;
}

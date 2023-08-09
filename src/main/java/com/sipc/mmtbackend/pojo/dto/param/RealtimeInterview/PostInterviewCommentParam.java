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
    // 面试官期待的部门
    private Integer expectDepartment;
    // 面试问题的回答
    private List<InterviewEvaluationPo> evaluations;
    // 综合评价
    private String overview;
}

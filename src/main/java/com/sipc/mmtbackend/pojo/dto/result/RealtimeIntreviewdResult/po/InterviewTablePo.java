package com.sipc.mmtbackend.pojo.dto.result.RealtimeIntreviewdResult.po;

import lombok.Data;

import java.util.List;

/**
 * 面试评价表
 */
@Data
public class InterviewTablePo {
    // 面试轮次
    private Integer round;
    // 是否可编辑
    private Boolean editable;
    // 是否实名评价
    private Boolean realName;
    // 是否通过（1 通过，2失败，3待定）
    private Integer isPass;
    // 面试官期待的部门
    private Integer expectDepartment;
    // 问题数量
    private Integer count;
    // 问题与答案
    private List<QuestionAndAnswerPo> questions;
}

package com.sipc.mmtbackend.pojo.dto.param.RealtimeInterview.po;

import com.sipc.mmtbackend.pojo.dto.result.RealtimeIntreviewdResult.po.MultipleChoiceAnswerPo;
import lombok.Data;

/**
 * 面试评价
 */
@Data
public class InterviewEvaluationPo {
    // 问题ID
    private Integer id;
    // 输入型回答（文本框）
    private String aStr;
    // 数字型回答（打分、量表）
    private Integer aInt;
    // 选项型回答（单选、多选、级联选择）
    private MultipleChoiceAnswerPo aSelect;
}

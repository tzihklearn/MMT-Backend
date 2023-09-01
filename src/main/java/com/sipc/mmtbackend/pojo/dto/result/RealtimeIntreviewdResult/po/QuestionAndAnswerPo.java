package com.sipc.mmtbackend.pojo.dto.result.RealtimeIntreviewdResult.po;

import com.sipc.mmtbackend.pojo.dto.data.QuestionValueListData;
import lombok.Data;

@Data
public class QuestionAndAnswerPo {
    // 问题ID（question_data的ID）
    private Integer id;
    // 问题在当前分组的序号
    private Integer order;
    // 问题分组（1面试基本评价，2面试综合评价，3面试问题）
    private Integer qType;
    // 问题类型（1单选，2多选，3下拉框，4输入框，5级联选择器，6量表题）
    private Integer type;
    // 问题题目
    private String question;
    // 量表题、打分题满分
    private Integer qMaxScore;
    // 单选、多选的选项
    private QuestionValueListData qOpts;
    // 面试问题参考
    private String qHint;
    // 输入型回答（文本框）
    private String aStr;
    // 数字型回答（打分、量表）
    private Integer aInt;
    // 选项型回答（单选、多选）
    private MultipleChoiceAnswerPo aSelect;
}

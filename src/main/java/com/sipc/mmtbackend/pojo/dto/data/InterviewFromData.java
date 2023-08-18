package com.sipc.mmtbackend.pojo.dto.data;

import lombok.Data;

import java.util.List;

/**
 * @author tzih
 * @version v1.0
 * @since 2023.08.17
 */
@Data
public class InterviewFromData {

    private Integer round;

    private List<QuestionPoData> basicEvaluationList;

    private List<QuestionPoData> comprehensiveEvaluationList;

    private List<QuestionPoData> interviewQuestionList;

}

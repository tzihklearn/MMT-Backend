package com.sipc.mmtbackend.pojo.dto.result.superAdmin.po;

import com.sipc.mmtbackend.pojo.dto.data.QuestionPoData;
import lombok.Data;

import java.util.List;

/**
 * @author tzih
 * @version v1.0
 * @since 2023.08.17
 */
@Data
public class InterviewQuestionPo {

    private Integer round;

    private List<QuestionPoData> basicEvaluationList;

    private List<QuestionPoData> comprehensiveEvaluationList;

    private List<QuestionPoData> interviewQuestionList;

}

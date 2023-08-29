package com.sipc.mmtbackend.pojo.domain.po.interviewReview;

import lombok.Data;

/**
 * @author tzih
 * @version v1.0
 * @since 2023.08.29
 */
@Data
public class QuestionScorePo {

    private Integer userId;

    private Integer interviewQuestionId;

    private Double score;

}

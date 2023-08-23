package com.sipc.mmtbackend.pojo.dto.result.dataDashboard.po;

import lombok.Data;

import java.util.List;

/**
 * @author tzih
 * @version v1.0
 * @since 2023.08.23
 */
@Data
public class InterviewGradingPo {

    private Integer rank;

    private Interviewer interviewer;

    private List<QuestionScorePo> questionPoList;

}

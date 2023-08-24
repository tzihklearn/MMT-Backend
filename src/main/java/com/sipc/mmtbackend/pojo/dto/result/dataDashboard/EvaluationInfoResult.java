package com.sipc.mmtbackend.pojo.dto.result.dataDashboard;

import com.sipc.mmtbackend.pojo.dto.result.dataDashboard.po.InterviewGradingPo;
import com.sipc.mmtbackend.pojo.dto.result.dataDashboard.po.InterviewerOpinionPo;
import lombok.Data;

import java.util.List;

/**
 * @author tzih
 * @version v1.0
 * @since 2023.08.23
 */
@Data
public class EvaluationInfoResult {

    private Integer status;

    private Integer round;

    private List<InterviewerOpinionPo> interviewResult;

    private String PassDepartment;

    private Boolean isTransfers;

    private List<InterviewerOpinionPo> passResult;

    private InterviewGradingPo interviewGradingPo;

    private List<InterviewerOpinionPo> comprehensiveQuestionList;


}

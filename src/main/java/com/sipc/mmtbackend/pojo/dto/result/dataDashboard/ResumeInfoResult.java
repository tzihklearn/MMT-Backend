package com.sipc.mmtbackend.pojo.dto.result.dataDashboard;

import com.sipc.mmtbackend.pojo.dto.result.dataDashboard.po.*;
import lombok.Data;

import java.util.List;

/**
 * @author tzih
 * @version v1.0
 * @since 2023.08.19
 */
@Data
public class ResumeInfoResult {

    private BasicQuestionPo basicQuestion;

    private DepartmentQuestionPo departmentQuestion;

    private ComprehensiveQuestionPo comprehensiveQuestion;

    private List<InterviewFeedbackPo> interviewFeedbackList;

    private InterviewFeedbackPo signIn;

    private List<InterviewArrangementPo> InterviewArrangementList;

}

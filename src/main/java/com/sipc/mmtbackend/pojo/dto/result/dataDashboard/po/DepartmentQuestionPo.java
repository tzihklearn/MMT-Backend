package com.sipc.mmtbackend.pojo.dto.result.dataDashboard.po;

import lombok.Data;

import java.util.List;

/**
 * @author tzih
 * @version v1.0
 * @since 2023.08.19
 */
@Data
public class DepartmentQuestionPo {

    private String departmentName;

    private Boolean isTransfers;

    private List<QuestionAnswerPo> questionList;

}

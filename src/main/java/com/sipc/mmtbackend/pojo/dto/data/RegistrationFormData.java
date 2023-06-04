package com.sipc.mmtbackend.pojo.dto.data;

import lombok.Data;

import java.util.List;

/**
 * @author tzih
 * @version v1.0
 * @since 2023.06.04
 */
@Data
public class RegistrationFormData {

    private Integer departmentNum;

    private Integer maxDepartmentNum;

    private Boolean isTransfers;

    private List<QuestionPoData> essentialQuestionList;

    private List<QuestionPoData> departmentQuestionList;

    private List<QuestionPoData> comprehensiveQuestionList;

}

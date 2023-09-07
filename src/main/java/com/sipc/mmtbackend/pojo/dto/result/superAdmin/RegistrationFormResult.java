package com.sipc.mmtbackend.pojo.dto.result.superAdmin;

import com.sipc.mmtbackend.pojo.dto.data.DepartmentQuestionData;
import com.sipc.mmtbackend.pojo.dto.data.QuestionPoData;
import lombok.Data;

import java.util.List;

/**
 * @author tzih
 * @version v1.0
 * @since 2023.06.04
 */
@Data
public class RegistrationFormResult {

    private Integer departmentNum;

    private Integer maxDepartmentNum;

    private Boolean isTransfers;

    private List<QuestionPoData> essentialQuestionList;

    private List<DepartmentQuestionData> departmentQuestionList;

    private List<QuestionPoData> comprehensiveQuestionList;

}

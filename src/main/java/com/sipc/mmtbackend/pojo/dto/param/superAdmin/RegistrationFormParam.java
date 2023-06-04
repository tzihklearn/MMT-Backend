package com.sipc.mmtbackend.pojo.dto.param.superAdmin;

import com.sipc.mmtbackend.pojo.dto.data.QuestionPoData;
import lombok.Data;

import java.util.List;

/**
 * @author tzih
 * @version v1.0
 * @since 2023.06.04
 */
@Data
public class RegistrationFormParam {

    private Integer departmentNum;

    private Integer maxDepartmentNum;

    private Boolean isTransfers;

    private List<QuestionPoData> essentialQuestionList;

    private List<QuestionPoData> departmentQuestionList;

    private List<QuestionPoData> comprehensiveQuestionList;

}

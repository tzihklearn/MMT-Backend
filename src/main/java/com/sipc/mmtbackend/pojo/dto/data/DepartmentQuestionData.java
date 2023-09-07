package com.sipc.mmtbackend.pojo.dto.data;

import lombok.Data;

import java.util.List;

/**
 * @author tzih
 * @version v1.0
 * @since 2023.09.05
 */
@Data
public class DepartmentQuestionData {

    private Integer departmentId;

    private List<QuestionPoData> questionList;

}

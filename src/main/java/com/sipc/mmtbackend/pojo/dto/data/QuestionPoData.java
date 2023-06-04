package com.sipc.mmtbackend.pojo.dto.data;

import lombok.Data;

/**
 * @author tzih
 * @version v1.0
 * @since 2023.05.03
 */
@Data
public class QuestionPoData {

    private Integer departmentId;

    private String name;

    private Boolean type;

    private Integer selectType;

    private QuestionValueListData selectValue;

}

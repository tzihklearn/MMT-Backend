package com.sipc.mmtbackend.pojo.dto.data;

import lombok.Data;

/**
 * @author tzih
 * @version v1.0
 * @since 2023.05.03
 */
@Data
public class QuestionPoData {

    private Integer id;

    private Integer departmentId;

    private String name;

    private Integer type;

    private Integer selectType;

    private Integer num;

    private String answer;

    private QuestionValueListData selectValue;

}

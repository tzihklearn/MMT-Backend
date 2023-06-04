package com.sipc.mmtbackend.pojo.dto.data;

import lombok.Data;

import java.util.List;

/**
 * @author tzih
 * @version v1.0
 * @since 2023.06.02
 */
@Data
public class QuestionValueData {

    private String value;

    private List<QuestionValueData> childValueList;

}

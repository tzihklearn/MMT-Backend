package com.sipc.mmtbackend.pojo.dto.data;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import java.util.List;

/**
 * @author tzih
 * @version v1.0
 * @since 2023.06.04
 */
@Data
public class QuestionValueListData {

    @JsonAlias("valueList")
    List<QuestionValueData> questionValueDataList;

}

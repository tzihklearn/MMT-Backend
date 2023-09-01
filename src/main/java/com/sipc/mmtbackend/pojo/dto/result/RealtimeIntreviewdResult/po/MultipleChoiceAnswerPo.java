package com.sipc.mmtbackend.pojo.dto.result.RealtimeIntreviewdResult.po;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import java.util.List;

@Data
public class MultipleChoiceAnswerPo {
    @JsonAlias("answerList")
    List<String> answerList;
}

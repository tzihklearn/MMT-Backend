package com.sipc.mmtbackend.pojo.dto.result.dataDashboard.po;

import lombok.Data;

import java.util.List;

/**
 * @author tzih
 * @version v1.0
 * @since 2023.08.23
 */
@Data
public class QuestionScorePo {

    private String question;

    private List<Double> score;

}

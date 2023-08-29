package com.sipc.mmtbackend.pojo.dto.result.interviewreview.po;

import lombok.Data;

import java.util.List;

/**
 * @author tzih
 * @version v1.0
 * @since 2023.08.28
 */
@Data
public class InfoPo {

    private Integer id;

    private Integer userId;

    private String studentId;

    private String name;

    private String className;

    private List<ScorePo> score;

    private Integer stateId;

    private String state;

    private Integer messageStateId;

    private String messageState;

//    private Boolean isMessage;

}

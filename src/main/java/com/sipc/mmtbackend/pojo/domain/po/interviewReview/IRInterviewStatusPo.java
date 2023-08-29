package com.sipc.mmtbackend.pojo.domain.po.interviewReview;

import lombok.Data;


/**
 * @author tzih
 * @version v1.0
 * @since 2023.08.28
 */
@Data
public class IRInterviewStatusPo {

    private Integer id;

    /**
     * 面试者id
     */
    private Integer userId;

    private Integer state;


    private String studentId;

    private String name;

    private Integer majorClassId;

    private Integer isMessage;
}

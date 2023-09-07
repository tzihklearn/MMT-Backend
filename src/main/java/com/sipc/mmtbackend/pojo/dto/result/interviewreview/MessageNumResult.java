package com.sipc.mmtbackend.pojo.dto.result.interviewreview;

import lombok.Data;

/**
 * @author tzih
 * @version v1.0
 * @since 2023.09.07
 */
@Data
public class MessageNumResult {

    private Integer notifiedNum;

    private Integer allNum;

    private Integer NotNotifiedNum;

}

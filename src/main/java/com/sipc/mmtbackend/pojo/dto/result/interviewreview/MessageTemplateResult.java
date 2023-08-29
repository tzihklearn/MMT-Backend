package com.sipc.mmtbackend.pojo.dto.result.interviewreview;

import lombok.Data;

/**
 * @author tzih
 * @version v1.0
 * @since 2023.08.28
 */
@Data
public class MessageTemplateResult {

    private String messageTemplate;

    private Integer notifiedNum;

    private Integer allNum;

    private Integer NotNotifiedNum;


}

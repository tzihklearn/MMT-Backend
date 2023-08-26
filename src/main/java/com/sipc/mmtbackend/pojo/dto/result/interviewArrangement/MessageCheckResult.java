package com.sipc.mmtbackend.pojo.dto.result.interviewArrangement;

import lombok.Data;

/**
 * @author tzih
 * @version v1.0
 * @since 2023.08.23
 */
@Data
public class MessageCheckResult {

    private String messageTemple;

    private Integer notifiedNum;

    private Integer allNum;

    private Integer NotNotifiedNum;

}

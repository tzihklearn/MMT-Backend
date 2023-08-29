package com.sipc.mmtbackend.pojo.dto.result.interviewreview.po;

import lombok.Data;

/**
 * @author tzih
 * @version v1.0
 * @since 2023.08.28
 */
@Data
public class StateAllPo {

    private StatePo notOperated;

    private StatePo fail;

    private StatePo pass;

    private StatePo undetermined;

}

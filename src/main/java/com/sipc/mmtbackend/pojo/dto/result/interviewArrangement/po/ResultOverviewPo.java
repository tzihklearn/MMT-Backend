package com.sipc.mmtbackend.pojo.dto.result.interviewArrangement.po;

import com.sipc.mmtbackend.pojo.dto.result.interviewreview.po.PieChartPo;
import lombok.Data;

/**
 * @author tzih
 * @version v1.0
 * @since 2023.08.29
 */
@Data
public class ResultOverviewPo {

    private PieChartPo notOperated;

    private PieChartPo undetermined;

    private PieChartPo pass;

    private PieChartPo fail;

    private Integer num;
}

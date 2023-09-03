package com.sipc.mmtbackend.pojo.dto.result.interviewreview.po;

import com.sipc.mmtbackend.pojo.dto.result.interviewArrangement.po.ResultOverviewPo;
import lombok.Data;

/**
 * @author tzih
 * @version v1.0
 * @since 2023.08.28
 */
@Data
public class PieChatAllPo {

    private ResultOverviewPo resultOverview;

    private PieChartOnPo departmentDivide;

    private PieChartOnPo addressDivide;

}

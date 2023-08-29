package com.sipc.mmtbackend.pojo.dto.result.interviewreview.po;

import lombok.Data;

import java.util.List;

/**
 * @author tzih
 * @version v1.0
 * @since 2023.08.28
 */
@Data
public class PieChatAllPo {

    private List<PieChartPo> resultOverview;

    private List<PieChartPo> departmentDivide;

    private List<PieChartPo> addressDivide;

}

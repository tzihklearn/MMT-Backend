package com.sipc.mmtbackend.pojo.dto.result.interviewreview.po;

import lombok.Data;

import java.util.List;

/**
 * @author tzih
 * @version v1.0
 * @since 2023.08.30
 */
@Data
public class PieChartOnPo {

    private List<PieChartPo> divide;

    private Integer num;

}

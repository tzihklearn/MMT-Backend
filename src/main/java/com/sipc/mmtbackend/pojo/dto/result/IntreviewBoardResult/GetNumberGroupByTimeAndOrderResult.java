package com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardResult;

import com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardResult.po.LineChartLineDataPo;
import lombok.Data;

import java.util.List;

@Data
public class GetNumberGroupByTimeAndOrderResult {
    // 日期（横坐标）
    private List<String> date;
    // 每个志愿的数据
    private List<LineChartLineDataPo> orders;
}

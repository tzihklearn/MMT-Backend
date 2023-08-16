package com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardBeforeResult;

import com.sipc.mmtbackend.pojo.dto.result.po.LineChartLineDataPo;
import lombok.Data;

import java.util.List;

@Data
public class GetNumberGroupByTimeAndOrderResult {
    // 日期（横坐标）
    private List<String> date;
    // 每个志愿的数据
    private List<LineChartLineDataPo> orders;
}

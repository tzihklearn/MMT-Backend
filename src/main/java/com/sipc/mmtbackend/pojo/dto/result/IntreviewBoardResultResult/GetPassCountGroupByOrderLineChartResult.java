package com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardResultResult;

import com.sipc.mmtbackend.pojo.dto.result.po.LineChartLineDataPo;
import lombok.Data;

import java.util.List;

@Data
public class GetPassCountGroupByOrderLineChartResult {
    // 面试轮次（横坐标）
    private List<String> round;
    // 每个志愿的数据
    private List<LineChartLineDataPo> departments;
}

package com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardResultResult.po;

import com.sipc.mmtbackend.pojo.dto.result.po.LineChartLineDataPo;
import lombok.Data;

import java.util.List;

@Data
public class GetPassCountGroupByDepartmentResult {
    // 面试轮次（横坐标）
    private List<String> round;
    // 每个部门的数据
    private List<LineChartLineDataPo> departments;
}

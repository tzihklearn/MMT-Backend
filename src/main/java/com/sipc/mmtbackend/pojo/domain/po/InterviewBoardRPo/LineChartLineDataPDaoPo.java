package com.sipc.mmtbackend.pojo.domain.po.InterviewBoardRPo;

import lombok.Data;

@Data
public class LineChartLineDataPDaoPo {
    // 横坐标（面试轮次）
    private Integer round;
    // 纵坐标（数据）
    private int number;
}

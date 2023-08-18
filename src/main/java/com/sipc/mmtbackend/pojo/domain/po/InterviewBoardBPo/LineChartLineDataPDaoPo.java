package com.sipc.mmtbackend.pojo.domain.po.InterviewBoardBPo;

import lombok.Data;

@Data
public class LineChartLineDataPDaoPo {
    // 横坐标（日期）
    private String date;
    // 纵坐标（数据）
    private int number;
}

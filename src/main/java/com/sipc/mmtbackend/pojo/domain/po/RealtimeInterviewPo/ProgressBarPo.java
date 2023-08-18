package com.sipc.mmtbackend.pojo.domain.po.RealtimeInterviewPo;

import lombok.Data;

@Data
public class ProgressBarPo {
    // 时间段
    private Integer hour;
    // 总人数
    private Integer total;
    // 已完成人数
    private Integer finished;
}

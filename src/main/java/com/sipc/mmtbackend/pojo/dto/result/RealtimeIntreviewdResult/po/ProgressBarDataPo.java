package com.sipc.mmtbackend.pojo.dto.result.RealtimeIntreviewdResult.po;

import lombok.Data;

@Data
public class ProgressBarDataPo {
    // 开始小时数
    private Integer time;
    // 总人数
    private Integer total;
    // 已完成人数
    private Integer finished;
}

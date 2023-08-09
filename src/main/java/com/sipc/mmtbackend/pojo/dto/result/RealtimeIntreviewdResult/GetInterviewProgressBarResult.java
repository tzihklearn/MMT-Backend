package com.sipc.mmtbackend.pojo.dto.result.RealtimeIntreviewdResult;

import com.sipc.mmtbackend.pojo.dto.result.RealtimeIntreviewdResult.po.ProgressBarDataPo;
import lombok.Data;

import java.util.List;

@Data
public class GetInterviewProgressBarResult {
    // 分组数量
    private Integer groupNum;
    // 每个分组的数据
    List<ProgressBarDataPo> bars;
}

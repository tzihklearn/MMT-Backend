package com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardMiddleResult;

import com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardMiddleResult.po.CheckinInfoPo;
import lombok.Data;

import java.util.List;

@Data
public class GetCheckinListResult {
    // 已签到人数
    private Integer count;
    // 已签到信息
    private List<CheckinInfoPo> checkins;
}

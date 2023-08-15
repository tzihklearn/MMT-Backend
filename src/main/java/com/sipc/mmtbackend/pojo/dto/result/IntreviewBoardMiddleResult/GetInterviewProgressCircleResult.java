package com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardMiddleResult;

import com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardMiddleResult.po.InterviewRoomProgressPo;
import lombok.Data;

import java.util.List;

@Data
public class GetInterviewProgressCircleResult {
    private Integer count;
    private List<InterviewRoomProgressPo> rooms;
}

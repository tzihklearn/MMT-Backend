package com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardResultResult;

import lombok.Data;

@Data
public class GetInterviewResultDataResult {
    // 报名人数
    private Integer signupCount;
    // 上一轮通过人数
    private Integer lastPassedCount;
    // 本轮签到人数
    private Integer checkinCount;
    // 最终通过人数
    private Integer passedCount;
}

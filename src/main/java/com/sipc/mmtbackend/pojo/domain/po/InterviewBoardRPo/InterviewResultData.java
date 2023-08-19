package com.sipc.mmtbackend.pojo.domain.po.InterviewBoardRPo;

import lombok.Data;

@Data
public class InterviewResultData {
    // 报名人数
    private Integer signupCount;
    // 上一轮通过人数
    private Integer lastPassedCount;
    // 本轮签到人数
    private Integer checkinCount;
    // 最终通过人数
    private Integer passedCount;
}

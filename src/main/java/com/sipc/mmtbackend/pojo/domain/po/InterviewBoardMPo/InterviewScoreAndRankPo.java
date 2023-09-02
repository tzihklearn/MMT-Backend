package com.sipc.mmtbackend.pojo.domain.po.InterviewBoardMPo;

import lombok.Data;

@Data
public class InterviewScoreAndRankPo {
    // 排名
    private Integer rank;
    // 姓名
    private String name;
    // 得分
    private Integer score;
}

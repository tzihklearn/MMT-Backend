package com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardMiddleResult.po;

import lombok.Data;

@Data
public class RankAndScorePo {
    // 排名
    private Integer rank;
    // 姓名
    private String name;
    // 得分
    private Integer score;
}

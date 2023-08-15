package com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardMiddleResult;

import com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardMiddleResult.po.RankAndScorePo;
import lombok.Data;

import java.util.List;

@Data
public class GetInterviewRankAndScoreResult {
    // 总页数
    private Integer pages;
    // 本页数据量
    private Integer count;
    // 面试排名与得分
    private List<RankAndScorePo> score;
}

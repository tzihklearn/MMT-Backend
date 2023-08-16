package com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardResultResult;

import com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardResultResult.po.RankAndScorePo;
import lombok.Data;

import java.util.List;

@Data
public class GetPassedRankAndScoreResult {
    // 总页数
    private Integer pages;
    // 本页数据量
    private Integer count;
    // 面试排名与得分
    private List<RankAndScorePo> score;
}

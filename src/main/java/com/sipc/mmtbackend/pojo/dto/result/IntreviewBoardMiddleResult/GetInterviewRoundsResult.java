package com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardMiddleResult;

import com.sipc.mmtbackend.pojo.dto.result.KVPo;
import lombok.Data;

import java.util.List;

@Data
public class GetInterviewRoundsResult {
    private Integer count;
    private List<KVPo> rounds;
}

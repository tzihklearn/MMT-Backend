package com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardResultResult;

import com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardResultResult.po.OrderPassCountPo;
import lombok.Data;

import java.util.List;

@Data
public class GetOrderPassCountResult {
    // 志愿数量
    private Integer count;
    private List<OrderPassCountPo> orders;
}

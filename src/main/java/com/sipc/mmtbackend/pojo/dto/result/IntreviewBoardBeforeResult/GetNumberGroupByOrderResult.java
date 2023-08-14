package com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardBeforeResult;

import com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardBeforeResult.po.GetNumberGroupByOrderPo;
import lombok.Data;

import java.util.List;

@Data
public class GetNumberGroupByOrderResult {
    // 志愿数量
    private Integer orderNum;
    // 报名总人数
    private Integer totalNum;
    private List<GetNumberGroupByOrderPo> nums;
}

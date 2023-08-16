package com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardResult;

import lombok.Data;

@Data
public class GetIntreviewStatusResult {
    // 纳新状态，0未开始纳新或纳新已结束，1未开始面试，2正在面试，3面试结束
    private Integer status;
}

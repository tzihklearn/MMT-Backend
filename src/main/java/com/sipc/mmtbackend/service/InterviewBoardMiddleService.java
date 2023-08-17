package com.sipc.mmtbackend.service;

import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardMiddleResult.GetInterviewRoundsResult;

public interface InterviewBoardMiddleService {
    /**
     * 获取当前组织已开始的面试轮次
     *
     * @return 面试轮次
     */
    CommonResult<GetInterviewRoundsResult> getInterviewRounds();
}

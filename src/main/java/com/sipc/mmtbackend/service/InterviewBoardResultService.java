package com.sipc.mmtbackend.service;

import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardResultResult.GetInterviewResultDataResult;

public interface InterviewBoardResultService {
    /**
     * 获取面试最终数据
     *
     * @param departmentId 部门ID
     * @return 面试最终数据
     */
    CommonResult<GetInterviewResultDataResult> getResultData(int departmentId);
}

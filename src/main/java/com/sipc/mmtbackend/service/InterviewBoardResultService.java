package com.sipc.mmtbackend.service;

import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardResultResult.GetDepartmentPassCountResult;
import com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardResultResult.GetInterviewResultDataResult;

public interface InterviewBoardResultService {
    /**
     * 获取面试最终数据
     *
     * @param departmentId 部门ID
     * @return 面试最终数据
     */
    CommonResult<GetInterviewResultDataResult> getResultData(int departmentId);

    /**
     * 获取最终各部门通过人数（组织饼图）
     *
     * @return 各部门通过人数
     */
    CommonResult<GetDepartmentPassCountResult> getDepartmentPassCount();
}

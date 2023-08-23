package com.sipc.mmtbackend.service;

import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardResult.GetDepartmentsResult;
import com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardResult.GetIntreviewStatusResult;

public interface InterviewBoardService {
    /**
     * 获取当前登录组织的部门列表
     *
     * @return 当前登录组织的部门列表
     */
    CommonResult<GetDepartmentsResult> getDepartments();

    /**
     * 获取当前纳新状态
     *
     * @return 当前纳新状态 0未开始，1正在进行，2已结束
     */
    CommonResult<GetIntreviewStatusResult> getInterviewStatus();
}

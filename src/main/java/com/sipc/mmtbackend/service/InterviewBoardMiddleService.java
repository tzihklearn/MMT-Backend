package com.sipc.mmtbackend.service;

import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardMiddleResult.GetCheckinListResult;
import com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardMiddleResult.GetInterviewProgressCircleResult;
import com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardMiddleResult.GetInterviewRoundsResult;

public interface InterviewBoardMiddleService {
    /**
     * 获取当前组织已开始的面试轮次
     *
     * @return 面试轮次
     */
    CommonResult<GetInterviewRoundsResult> getInterviewRounds();

    /**
     * 获取已签到人员列表
     *
     * @param round 面试轮次，默认为1
     * @param departmentId 部门 ID，默认0（全部部门）
     * @return 签到信息
     */
    CommonResult<GetCheckinListResult> getCheckinList(int round, int departmentId);

    /**
     * 获取面试进度
     *
     * @param round 面试轮次，默认为1
     * @param departmentId 部门 ID，默认0（全部部门）
     * @return 各个面试地点的面试进度
     */
    CommonResult<GetInterviewProgressCircleResult> getInterviewProgressCircle(int round, int departmentId);
}

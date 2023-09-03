package com.sipc.mmtbackend.controller.InterviewBoard;

import com.sipc.mmtbackend.annotation.BPermission;
import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardMiddleResult.GetCheckinListResult;
import com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardMiddleResult.GetInterviewProgressCircleResult;
import com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardMiddleResult.GetInterviewRankAndScoreResult;
import com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardMiddleResult.GetInterviewRoundsResult;
import com.sipc.mmtbackend.service.InterviewBoardMiddleService;
import com.sipc.mmtbackend.utils.CheckroleBUtil.pojo.PermissionEnum;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/b/interview/middle")
@BPermission(PermissionEnum.MEMBER)
public class InterviewBoardMiddleController {
    @Resource
    InterviewBoardMiddleService interviewBoardMiddleService;

    /**
     * 获取当前组织已开始的面试轮次
     *
     * @return 面试轮次
     */
    @GetMapping("/rounds")
    public CommonResult<GetInterviewRoundsResult> getInterviewRounds(){
        return interviewBoardMiddleService.getInterviewRounds();
    }

    /**
     * 获取面试进度
     *
     * @param round 面试轮次，默认为1
     * @param departmentId 部门 ID，默认0（全部部门）
     * @return 各个面试地点的面试进度
     */
    @GetMapping("/progress")
    public CommonResult<GetInterviewProgressCircleResult> getInterviewProgressCircle(
            @RequestParam(value = "round", defaultValue = "1") int round,
            @RequestParam(value = "department", defaultValue = "0") int departmentId
    ){
        return interviewBoardMiddleService.getInterviewProgressCircle(round, departmentId);
    }

    /**
     * 获取已签到人员列表
     *
     * @param round 面试轮次，默认为1
     * @param departmentId 部门 ID，默认0（全部部门）
     * @return 签到信息
     */
    @GetMapping("/checkin")
    public CommonResult<GetCheckinListResult> getCheckinList(
            @RequestParam(value = "round", defaultValue = "1") int round,
            @RequestParam(value = "department", defaultValue = "0") int departmentId
    ){
        return interviewBoardMiddleService.getCheckinList(round, departmentId);
    }

    /**
     * 获取面试分数与排名
     *
     * @param round 面试轮次，默认为1
     * @param departmentId 部门 ID，默认0（全部部门）
     * @param pageId 页码，默认1
     * @return 排名与分数
     */
    @GetMapping("/rank")
    public CommonResult<GetInterviewRankAndScoreResult> getInterviewRankAndScore(
            @RequestParam(value = "round", defaultValue = "1") int round,
            @RequestParam(value = "department", defaultValue = "0") int departmentId,
            @RequestParam(value = "page", defaultValue = "1") int pageId
    ){
        return interviewBoardMiddleService.getInterviewRankAndScore(round, departmentId, pageId);
    }
}

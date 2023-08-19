package com.sipc.mmtbackend.controller.InterviewBoard;

import com.sipc.mmtbackend.annotation.BPermission;
import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardResultResult.*;
import com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardResultResult.po.GetPassCountGroupByDepartmentResult;
import com.sipc.mmtbackend.service.InterviewBoardResultService;
import com.sipc.mmtbackend.utils.CheckroleBUtil.pojo.PermissionEnum;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/b/interview/result")
@BPermission(PermissionEnum.MEMBER)
public class InterviewBoardResultController {
    @Resource
    InterviewBoardResultService interviewBoardResultService;


    /**
     * 获取面试最终数据
     *
     * @param departmentId 部门ID
     * @return 面试最终数据
     */
    @GetMapping("/result")
    public CommonResult<GetInterviewResultDataResult> getResultData(
            @RequestParam(value = "department", defaultValue = "0") int departmentId
    ){
        return interviewBoardResultService.getResultData(departmentId);
    }

    /**
     * 获取最终各部门通过人数（组织饼图）
     *
     * @return 各部门通过人数
     */
    @GetMapping("/depPass")
    public CommonResult<GetDepartmentPassCountResult> getDepartmentPassCount(){
        return null;
    }

    /**
     * 获取不同部门通过人数随时间变化折线图（组织折线图）
     *
     * @return 不同部门通过人数随时间变化折线图数据
     */
    @GetMapping("/orgLineChart")
    public CommonResult<GetPassCountGroupByDepartmentResult> getPassCountGroupByDepartmentLineChart(){
        return null;
    }

    /**
     * 获取面试通过者排名与分数
     *
     * @param departmentId 部门ID, 默认0
     * @param pageId 页码，默认1
     * @return 通过面试者排名与分数
     */
    @GetMapping("/rank")
    public CommonResult<GetPassedRankAndScoreResult> getPassedRankAndScore(
            @RequestParam(value = "department", defaultValue = "0") int departmentId,
            @RequestParam(value = "page", defaultValue = "1") int pageId
    ){
        return null;
    }

    /**
     * 获取最终各志愿通过人数（部门饼图）
     *
     * @param departmentId 部门ID
     * @return 最终各志愿通过人数
     */
    @GetMapping("/orderPass")
    public CommonResult<GetOrderPassCountResult> getOrderPassCount(
            @RequestParam(value = "department") int departmentId
    ){
        return null;
    }

    /**
     * 获取不同志愿通过人数随时间变化折线图（部门折线图）
     *
     * @param departmentId 组织ID
     * @return 不同志愿通过人数随时间变化情况
     */
    @GetMapping("/depLineChart")
    public CommonResult<GetPassCountGroupByOrderLineChartResult> getPassCountGroupByOrderLineChart(
            @RequestParam(value = "department") int departmentId
    ){
        return null;
    }
}

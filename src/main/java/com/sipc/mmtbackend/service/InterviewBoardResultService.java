package com.sipc.mmtbackend.service;

import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardResultResult.GetDepartmentPassCountResult;
import com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardResultResult.GetInterviewResultDataResult;
import com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardResultResult.GetOrderPassCountResult;
import com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardResultResult.GetPassCountGroupByOrderLineChartResult;
import com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardResultResult.po.GetPassCountGroupByDepartmentResult;

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

    /**
     * 获取不同部门通过人数随时间变化折线图（组织折线图）
     *
     * @return 不同部门通过人数随时间变化折线图数据
     */
    CommonResult<GetPassCountGroupByDepartmentResult> getPassCountGroupByDepartmentLineChart();

    /**
     * 获取最终各志愿通过人数（部门饼图）
     *
     * @param departmentId 部门ID
     * @return 最终各志愿通过人数
     */
    CommonResult<GetOrderPassCountResult> getOrderPassCount(int departmentId);

    /**
     * 获取不同志愿通过人数随时间变化折线图（部门折线图）
     *
     * @param departmentId 组织ID
     * @return 不同志愿通过人数随时间变化情况
     */
    CommonResult<GetPassCountGroupByOrderLineChartResult> getPassCountGroupByOrderLineChart(int departmentId);
}

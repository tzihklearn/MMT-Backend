package com.sipc.mmtbackend.service;

import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardBeforeResult.*;

public interface InterviewBoardBeforeService {

    /**
     * 查询当前登录组织各个部门已报名人数、第一志愿人数
     *
     * @return GetNumberGroupByDepartmentResult 组织已报名总人数、各个部门的人数与第一志愿人数
     */
    CommonResult<GetNumberGroupByDepartmentResult> getNumberGroupByDepartment();

    /**
     * 获取组织或指定部门的报名人数与第一志愿人数
     *
     * @param departmentId 组织ID
     * @return 总人数与第一志愿人数
     */
    CommonResult<GetSignUpNumResult> getDeptSignupNum(int departmentId);

    /**
     * 获取组织各个部门报名人数随时间变化情况
     *
     * @return 折线图横坐标（日期）、折线数据（折线名称与数据）
     */
    CommonResult<GetNumberGroupByTimeAndDepartmentResult> getNumberGroupByByTimeAndDepartment();

    /**
     * 获取指定部门不同志愿人数
     *
     * @param departmentId 部门 ID
     * @return 指定部门不同志愿人数
     */
    CommonResult<GetNumberGroupByOrderResult> getNumberGroupByOrder(Integer departmentId);

    /**
     * 获取指定部门不同志愿人数随时间变化情况
     *
     * @param departmentId 部门 ID
     * @return 不同志愿人数随时间变化情况
     */
    CommonResult<GetNumberGroupByTimeAndOrderResult> getNumberGroupByTimeAndOrder(Integer departmentId);
}

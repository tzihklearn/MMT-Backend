package com.sipc.mmtbackend.service;

import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardResult.GetDepartmentsResult;
import com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardResult.GetNumberGroupByDepartmentResult;
import com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardResult.GetNumberGroupByTimeAndDepartmentResult;
import com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardResult.GetSignUpNumResult;

public interface InterviewBoardService {
    /**
     * 获取当前登录组织的部门列表
     *
     * @return 当前登录组织的部门列表
     */
    CommonResult<GetDepartmentsResult> getDepartments();

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
    CommonResult<GetSignUpNumResult> getSignupNum(Integer departmentId);
    /**
     * 获取组织各个部门报名人数随时间变化情况（组织总况的折线图）
     *
     * @return 折线图横坐标（日期）、折线数据（折线名称与数据）
     */
    CommonResult<GetNumberGroupByTimeAndDepartmentResult> getNumberGroupByByTimeAndDepartment();
}

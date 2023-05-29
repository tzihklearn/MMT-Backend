package com.sipc.mmtbackend.service;

import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardResult.GetDepartmentsResult;
import com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardResult.GetNumberGroupByDepartment;
import com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardResult.GetSignUpNum;

public interface InterviewBoardService {
    /**
     * 获取当前登录组织的部门列表
     *
     * @return 当前登录组织的部门列表
     */
    CommonResult<GetDepartmentsResult> getDepartments();

    /**
     * 查询当前登录组织各个部门已报名人数
     *
     * @return GetNumberGroupByDepartment 组织已报名总人数、各个部门的人数
     */
    CommonResult<GetNumberGroupByDepartment> getNumberGroupByDepartment();

    /**
     * 获取组织或指定部门的报名人数与第一志愿人数
     *
     * @param departmentId 组织ID
     * @return 总人数与第一志愿人数
     */
    CommonResult<GetSignUpNum> getSignupNum(Integer departmentId);
}

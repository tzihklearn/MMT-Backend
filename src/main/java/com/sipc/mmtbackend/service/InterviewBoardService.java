package com.sipc.mmtbackend.service;

import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardResult.GetDepartmentsResult;
import com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardResult.GetNumberGroupByDepartment;

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
}

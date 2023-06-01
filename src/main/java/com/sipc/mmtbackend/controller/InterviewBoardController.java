package com.sipc.mmtbackend.controller;

import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardResult.GetDepartmentsResult;
import com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardResult.GetNumberGroupByDepartmentResult;
import com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardResult.GetSignUpNumResult;
import com.sipc.mmtbackend.service.InterviewBoardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/b/interview")
public class InterviewBoardController {
    @Resource
    InterviewBoardService interviewBoardService;

    /**
     * 获取当前登录组织的部门列表
     *
     * @return 当前登录组织的部门列表
     */
    @GetMapping("/departments")
    public CommonResult<GetDepartmentsResult> getDepartments() {
        return interviewBoardService.getDepartments();
    }

    /**
     * 查询当前登录组织各个部门已报名人数、第一志愿人数
     *
     * @return GetNumberGroupByDepartmentResult 组织已报名总人数、各个部门的人数与第一志愿人数
     */
    @GetMapping("/numGroupByDepartment")
    public CommonResult<GetNumberGroupByDepartmentResult> getNumberGroupByDepartment() {
        return interviewBoardService.getNumberGroupByDepartment();
    }

    /**
     * 获取组织或指定部门的报名人数与第一志愿人数
     *
     * @param departmentId 组织ID
     * @return 总人数与第一志愿人数
     */
    @GetMapping("/totalNum")
    public CommonResult<GetSignUpNumResult> getSignupNum(@RequestParam(value = "departmentId", required = false) Integer departmentId) {
        return interviewBoardService.getSignupNum(departmentId);
    }
}

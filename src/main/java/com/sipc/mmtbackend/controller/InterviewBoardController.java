package com.sipc.mmtbackend.controller;

import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardResult.*;
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
     * 查询当前登录组织各个部门已报名人数、第一志愿人数（组织饼图）
     *
     * @return GetNumberGroupByDepartmentResult 组织已报名总人数、各个部门的人数与第一志愿人数
     */
    @GetMapping("/orgPieChart")
    public CommonResult<GetNumberGroupByDepartmentResult> getNumberGroupByDepartment() {
        return interviewBoardService.getNumberGroupByDepartment();
    }

    /**
     * 获取组织或指定部门的报名人数与第一志愿人数（左上角）
     *
     * @param departmentId 组织ID
     * @return 总人数与第一志愿人数
     */
    @GetMapping("/numData")
    public CommonResult<GetSignUpNumResult> getDeptSignupNum(@RequestParam(value = "departmentId", required = false) Integer departmentId) {
        return interviewBoardService.getDeptSignupNum(departmentId);
    }

    /**
     * 获取组织各个部门报名人数随时间变化情况（组织折线图）
     *
     * @return 折线图横坐标（日期）、折线数据（折线名称与数据）
     */
    @GetMapping("/oegLineChart")
    public CommonResult<GetNumberGroupByTimeAndDepartmentResult> getNumberGroupByByTimeAndDepartment(){
        return interviewBoardService.getNumberGroupByByTimeAndDepartment();
    }
}

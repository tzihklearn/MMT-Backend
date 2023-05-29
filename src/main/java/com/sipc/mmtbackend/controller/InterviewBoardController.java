package com.sipc.mmtbackend.controller;

import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardResult.GetDepartmentsResult;
import com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardResult.GetNumberGroupByDepartment;
import com.sipc.mmtbackend.service.InterviewBoardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
     * 查询当前登录组织各个部门已报名人数
     *
     * @return GetNumberGroupByDepartment 组织已报名总人数、各个部门的人数
     */
    @GetMapping("/numGroupByDepartment")
    public CommonResult<GetNumberGroupByDepartment> getNumberGroupByDepartment() {
        return interviewBoardService.getNumberGroupByDepartment();
    }
}

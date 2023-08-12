package com.sipc.mmtbackend.controller;

import com.sipc.mmtbackend.annotation.BPermission;
import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardBeforeResult.*;
import com.sipc.mmtbackend.service.InterviewBoardBeforeService;
import com.sipc.mmtbackend.utils.CheckroleBUtil.pojo.PermissionEnum;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/b/interview/before")
@BPermission(PermissionEnum.MEMBER)
public class InterviewBoardBeforeController {
    @Resource
    InterviewBoardBeforeService interviewBoardBeforeService;

    /**
     * 查询当前登录组织各个部门已报名人数、第一志愿人数（组织饼图）
     *
     * @return GetNumberGroupByDepartmentResult 组织已报名总人数、各个部门的人数与第一志愿人数
     */
    @GetMapping("/orgPieChart")
    public CommonResult<GetNumberGroupByDepartmentResult> getNumberGroupByDepartment() {
        return interviewBoardBeforeService.getNumberGroupByDepartment();
    }

    /**
     * 获取组织或指定部门的报名人数与第一志愿人数（左上角）
     *
     * @param departmentId 组织ID
     * @return 总人数与第一志愿人数
     */
    @GetMapping("/numData")
    public CommonResult<GetSignUpNumResult> getDeptSignupNum(@RequestParam(value = "departmentId", required = false) Integer departmentId) {
        return interviewBoardBeforeService.getDeptSignupNum(departmentId);
    }

    /**
     * 获取组织各个部门报名人数随时间变化情况（组织折线图）
     *
     * @return 折线图横坐标（日期）、折线数据（折线名称与数据）
     */
    @GetMapping("/orgLineChart")
    public CommonResult<GetNumberGroupByTimeAndDepartmentResult> getNumberGroupByByTimeAndDepartment() {
        return interviewBoardBeforeService.getNumberGroupByByTimeAndDepartment();
    }

    /**
     * 获取指定组织不同志愿人数（部门饼图）
     *
     * @param departmentId 组织 ID
     * @return 指定组织不同志愿人数
     */
    @GetMapping("/depPieChart")
    public CommonResult<GetNumberGroupByOrderResult> getNumberGroupByChoise(@RequestParam(value = "departmentId") Integer departmentId) {
        return interviewBoardBeforeService.getNumberGroupByOrder(departmentId);
    }

    /**
     *获取指定部门各个志愿报名人数随时间变化情况（部门折线图）
     *
     * @param departmentId 部门ID
     * @return 折线图横坐标（日期）、折线数据（折线名称与数据）
     */
    @GetMapping("/depLineChart")
    public CommonResult<GetNumberGroupByTimeAndOrderResult> getNumberGroupByTimeAndOrder(@RequestParam(value = "departmentId") Integer departmentId){
        return interviewBoardBeforeService.getNumberGroupByTimeAndOrder(departmentId);
    }
}

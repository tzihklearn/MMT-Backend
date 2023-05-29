package com.sipc.mmtbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sipc.mmtbackend.mapper.DepartmentMapper;
import com.sipc.mmtbackend.pojo.domain.Department;
import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardResult.GetDepartmentsResult;
import com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardResult.po.GetDepartmentPo;
import com.sipc.mmtbackend.service.InterviewBoardService;
import com.sipc.mmtbackend.utils.CheckroleBUtil.pojo.BTokenSwapPo;
import com.sipc.mmtbackend.utils.ThreadLocalContextUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Slf4j
public class InterviewBoardServiceImpl implements InterviewBoardService {
    private final DepartmentMapper departmentMapper;

    /**
     * 获取当前登录组织的部门列表
     *
     * @return 当前登录组织的部门列表
     */
    @Override
    public CommonResult<GetDepartmentsResult> getDepartments() {
        BTokenSwapPo context = ThreadLocalContextUtil.getContext();
        List<Department> departmentList = departmentMapper.selectList(new QueryWrapper<Department>().eq("organization_id", context.getOrganizationId()));
        GetDepartmentsResult result = new GetDepartmentsResult();
        List<GetDepartmentPo> departments = new LinkedList<>();
        for (Department department : departmentList) {
            GetDepartmentPo gdp = new GetDepartmentPo();
            gdp.setDepartmentId(department.getId());
            gdp.setDepartmentName(department.getName());
            departments.add(gdp);
        }
        result.setNum(departments.size());
        result.setDepartments(departments);
        return CommonResult.success(result);
    }
}

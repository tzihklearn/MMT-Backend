package com.sipc.mmtbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sipc.mmtbackend.mapper.AdmissionMapper;
import com.sipc.mmtbackend.mapper.DepartmentMapper;
import com.sipc.mmtbackend.mapper.InterviewBoardDataMapper;
import com.sipc.mmtbackend.pojo.domain.Admission;
import com.sipc.mmtbackend.pojo.domain.Department;
import com.sipc.mmtbackend.pojo.domain.po.InterviewBoardPo.LineChartLineDataDaoPo;
import com.sipc.mmtbackend.pojo.domain.po.InterviewBoardPo.PersonNumGroupByDepartmentPo;
import com.sipc.mmtbackend.pojo.domain.po.InterviewBoardPo.PersonNumGroupByOrderPo;
import com.sipc.mmtbackend.pojo.domain.po.InterviewBoardPo.TotalNumPo;
import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardResult.*;
import com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardResult.po.GetDepartmentPo;
import com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardResult.po.GetNumberGroupByDepartmentPo;
import com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardResult.po.GetNumberGroupByOrderPo;
import com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardResult.po.LineChartLineDataPo;
import com.sipc.mmtbackend.service.InterviewBoardService;
import com.sipc.mmtbackend.utils.CheckroleBUtil.pojo.BTokenSwapPo;
import com.sipc.mmtbackend.utils.ThreadLocalContextUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Slf4j
public class InterviewBoardServiceImpl implements InterviewBoardService {
    private final DepartmentMapper departmentMapper;
    private final AdmissionMapper admissionMapper;
    private final InterviewBoardDataMapper interviewBoardDataMapper;

    /**
     * 获取当前登录组织的部门列表
     *
     * @return 当前登录组织的部门列表
     */
    @Override
    public CommonResult<GetDepartmentsResult> getDepartments() {
        BTokenSwapPo context = ThreadLocalContextUtil.getContext();
        List<Department> departmentList = departmentMapper.selectList(
                new QueryWrapper<Department>()
                        .eq("organization_id", context.getOrganizationId()));
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

    /**
     * 查询当前登录组织各个部门已报名人数、第一志愿人数
     *
     * @return GetNumberGroupByDepartmentResult 组织已报名总人数、各个部门的人数与第一志愿人数
     */
    @Override
    public CommonResult<GetNumberGroupByDepartmentResult> getNumberGroupByDepartment() {
        BTokenSwapPo context = ThreadLocalContextUtil.getContext();
        Admission admission = admissionMapper.selectOne(
                new QueryWrapper<Admission>()
                        .eq("organization_id", context.getOrganizationId())
                        .orderByDesc("id"));
        if (admission == null) {
            log.warn("用户 " + context + " 尝试在无活动的纳新时查询已报名人数");
            return CommonResult.fail("查询失败：未开始纳新或纳新已结束");
        }
        List<PersonNumGroupByDepartmentPo> deps =
                interviewBoardDataMapper.selectSignInPersonNumberGroupByDepartmentByAndAdmissionId(
                        admission.getId());
        GetNumberGroupByDepartmentResult result = new GetNumberGroupByDepartmentResult();
        result.setCNum(0);
        List<GetNumberGroupByDepartmentPo> results = new LinkedList<>();
        for (PersonNumGroupByDepartmentPo po : deps) {
            GetNumberGroupByDepartmentPo dep = new GetNumberGroupByDepartmentPo();
            dep.setNum(po.getNumber());
            dep.setDepartmentName(po.getDepartmentName());
            dep.setFirstChoiceNum(po.getFirstChoiceNum());
            result.setCNum(result.getCNum() + po.getNumber());
            results.add(dep);
        }
        result.setDepNum(results.size());
        return CommonResult.success(result);
    }

    /**
     * 获取组织或指定部门的报名人数与第一志愿人数
     *
     * @param departmentId 组织ID
     * @return 总人数与第一志愿人数
     */
    @Override
    public CommonResult<GetSignUpNumResult> getDeptSignupNum(Integer departmentId) {
        BTokenSwapPo context = ThreadLocalContextUtil.getContext();
        Admission admission = admissionMapper.selectOne(
                new QueryWrapper<Admission>()
                        .eq("organization_id", context.getOrganizationId())
                        .orderByDesc("id"));
        if (admission == null) {
            log.warn("用户 " + context + " 尝试在无活动的纳新时查询已报名人数");
            return CommonResult.fail("查询失败：未开始纳新或纳新已结束");
        }
        if (departmentId != null) {
            Department department = departmentMapper.selectById(departmentId);
            if (department == null || !Objects.equals(department.getOrganizationId(), context.getOrganizationId())) {
                log.info("B 端用户 " + context + " 尝试访问不存在或不属于已登录组织的部门 " + department + " 的信息");
                return CommonResult.fail("查询失败：部门不存在");
            }
        }
        TotalNumPo totalNumPo = interviewBoardDataMapper.selectTotalNumByDepartmentIdAndAdmissionId(departmentId, admission.getId());
        if (totalNumPo == null) {
            log.warn("用户" + context + "查询组织 " + departmentId + " 的报名人数时出现异常，数据库返回空");
            return CommonResult.serverError();
        }
        GetSignUpNumResult result = new GetSignUpNumResult();
        result.setFirstChoiceNum(totalNumPo.getFirstChoiceNum());
        result.setTotalNum(totalNumPo.getTotalNum());
        return CommonResult.success(result);
    }

    /**
     * 获取组织各个部门报名人数随时间变化情况（组织总况的折线图）
     *
     * @return 折线图横坐标（日期）、折线数据（折线名称与数据）
     */
    @Override
    public CommonResult<GetNumberGroupByTimeAndDepartmentResult> getNumberGroupByByTimeAndDepartment() {
        BTokenSwapPo context = ThreadLocalContextUtil.getContext();
        Admission admission = admissionMapper.selectOne(
                new QueryWrapper<Admission>()
                        .eq("organization_id", context.getOrganizationId())
                        .orderByDesc("id"));
        if (admission == null) {
            log.warn("用户 " + context + " 尝试在无活动的纳新时查询已报名人数");
            return CommonResult.fail("查询失败：未开始纳新或纳新已结束");
        }
        // 数据库查询到的数据
        List<LineChartLineDataDaoPo> daoDatas = interviewBoardDataMapper.selectInterviewNumberLineChartGroupByDepartmentDataByOrganizationIdAndAdmissionId(
                context.getOrganizationId(), admission.getId());
        // 响应体数据
        GetNumberGroupByTimeAndDepartmentResult result = new GetNumberGroupByTimeAndDepartmentResult();
        result.setDate(daoDatas.get(0).getAbscissaData());
        List<LineChartLineDataPo> departmentDates = new LinkedList<>();
        for (LineChartLineDataDaoPo data : daoDatas){
            LineChartLineDataPo depData = new LineChartLineDataPo();
            depData.setName(data.getName());
            depData.setData(data.getYDatas());
            departmentDates.add(depData);
        }
        result.setDepartments(departmentDates);
        return CommonResult.success(result);
    }

    /**
     * 获取指定组织不同志愿人数
     *
     * @param departmentId 组织 ID
     * @return 指定组织不同志愿人数
     */
    @Override
    public CommonResult<GetNumberGroupByOrderResult> getNumberGroupByOrder(Integer departmentId) {
        BTokenSwapPo context = ThreadLocalContextUtil.getContext();
        Admission admission = admissionMapper.selectOne(
                new QueryWrapper<Admission>()
                        .eq("organization_id", context.getOrganizationId())
                        .orderByDesc("id"));
        if (admission == null) {
            log.warn("用户 " + context + " 尝试在无活动的纳新时查询已报名人数");
            return CommonResult.fail("查询失败：未开始纳新或纳新已结束");
        }
        Department department = departmentMapper.selectById(departmentId);
        if (department == null || !Objects.equals(department.getOrganizationId(), context.getOrganizationId())){
            log.warn("用户 " + context + " 尝试在查询不存在的组织或不属于其部门的组织 " + departmentId + " 的信息");
            return CommonResult.fail("查询失败：部门不存在");
        }
        List<PersonNumGroupByOrderPo> depPos =
                interviewBoardDataMapper.selectNumberGroupByOrderByAdmissionIdAndDepartmentId(
                        admission.getId(), department.getId());
        GetNumberGroupByOrderResult result = new GetNumberGroupByOrderResult();
        result.setTotalNum(0);
        List<GetNumberGroupByOrderPo> results = new LinkedList<>();
        for (PersonNumGroupByOrderPo po : depPos){
            GetNumberGroupByOrderPo r = new GetNumberGroupByOrderPo();
            r.setOrderNum(po.getOrderNum());
            r.setNum(po.getNumber());
            results.add(r);
            result.setTotalNum(result.getTotalNum() + r.getNum());
        }
        result.setNums(results);
        result.setOrderNum(results.size());
        return CommonResult.success(result);
    }
}

package com.sipc.mmtbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sipc.mmtbackend.mapper.AdmissionMapper;
import com.sipc.mmtbackend.mapper.DepartmentMapper;
import com.sipc.mmtbackend.mapper.InterviewBoardBDataMapper;
import com.sipc.mmtbackend.pojo.domain.Admission;
import com.sipc.mmtbackend.pojo.domain.Department;
import com.sipc.mmtbackend.pojo.domain.po.InterviewBoardBPo.LineChartLineDataDaoPo;
import com.sipc.mmtbackend.pojo.domain.po.InterviewBoardBPo.PersonNumGroupByDepartmentPo;
import com.sipc.mmtbackend.pojo.domain.po.InterviewBoardBPo.PersonNumGroupByOrderPo;
import com.sipc.mmtbackend.pojo.domain.po.InterviewBoardBPo.TotalNumPo;
import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardBeforeResult.*;
import com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardBeforeResult.po.GetNumberGroupByDepartmentPo;
import com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardBeforeResult.po.GetNumberGroupByOrderPo;
import com.sipc.mmtbackend.pojo.dto.result.po.LineChartLineDataPo;
import com.sipc.mmtbackend.service.InterviewBoardBeforeService;
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
public class InterviewBoardBeforeServiceImpl implements InterviewBoardBeforeService {
    private final DepartmentMapper departmentMapper;
    private final AdmissionMapper admissionMapper;
    private final InterviewBoardBDataMapper interviewBoardBDataMapper;

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
                interviewBoardBDataMapper.selectSignInPersonNumberGroupByDepartmentByAndAdmissionId(
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
        result.setDepNums(results);
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
    public CommonResult<GetSignUpNumResult> getDeptSignupNum(int departmentId) {
        BTokenSwapPo context = ThreadLocalContextUtil.getContext();
        Admission admission = admissionMapper.selectOne(
                new QueryWrapper<Admission>()
                        .eq("organization_id", context.getOrganizationId())
                        .orderByDesc("id"));
        if (admission == null) {
            log.warn("用户 " + context + " 尝试在无活动的纳新时查询已报名人数");
            return CommonResult.fail("查询失败：未开始纳新或纳新已结束");
        }
        if (departmentId != 0) {
            Department department = departmentMapper.selectById(departmentId);
            if (department == null) {
                log.info("B 端用户 " + context + " 尝试访问不存在的部门 " + departmentId + " 的信息");
                return CommonResult.fail("查询失败：部门不存在或不属于当前组织");
            } else if (!Objects.equals(department.getOrganizationId(), context.getOrganizationId())){
                log.info("B 端用户 " + context + " 尝试访问不属于已登录组织的部门 " + department + " 的信息");
                return CommonResult.fail("查询失败：部门不存在或不属于当前组织");
            }
        }
        GetSignUpNumResult result = new GetSignUpNumResult();
        TotalNumPo totalNumPo = interviewBoardBDataMapper.selectTotalNumByDepartmentIdAndAdmissionId(departmentId, admission.getId());
        if (totalNumPo == null) {
            log.warn("用户" + context + "查询部门 " + departmentId + " 的报名人数时出现异常，数据库返回空");
            result.setFirstChoiceNum(0);
            result.setTotalNum(0);
            return CommonResult.success(result);
        }
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
        List<LineChartLineDataDaoPo> daoDatas = interviewBoardBDataMapper.selectInterviewNumberLineChartGroupByDepartmentDataByOrganizationIdAndAdmissionId(
                context.getOrganizationId(), admission.getId());
        // 响应体数据
        GetNumberGroupByTimeAndDepartmentResult result = new GetNumberGroupByTimeAndDepartmentResult();
        List<LineChartLineDataPo> departmentDates = new LinkedList<>();
        if (daoDatas.size() == 0){
            result.setDepartments(departmentDates);
            result.setDate(new ArrayList<>());
            return CommonResult.success(result);
        }
        result.setDate(daoDatas.get(0).getAbscissaData());
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
     * 获取指定部门不同志愿人数
     *
     * @param departmentId 部门 ID
     * @return 指定部门不同志愿人数
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
            if (department == null) {
                log.info("B 端用户 " + context + " 尝试访问不存在的部门 " + departmentId + " 的信息");
                return CommonResult.fail("查询失败：部门不存在或不属于当前组织");
            } else if (!Objects.equals(department.getOrganizationId(), context.getOrganizationId())){
                log.info("B 端用户 " + context + " 尝试访问不属于已登录组织的部门 " + department + " 的信息");
                return CommonResult.fail("查询失败：部门不存在或不属于当前组织");
            }
        }
        List<PersonNumGroupByOrderPo> depPos =
                interviewBoardBDataMapper.selectNumberGroupByOrderByAdmissionIdAndDepartmentId(
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

    /**
     * 获取指定部门不同志愿人数随时间变化情况
     *
     * @param departmentId 部门 ID
     * @return 不同志愿人数随时间变化情况
     */
    @Override
    public CommonResult<GetNumberGroupByTimeAndOrderResult> getNumberGroupByTimeAndOrder(Integer departmentId) {
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
            if (department == null) {
                log.info("B 端用户 " + context + " 尝试访问不存在的部门 " + departmentId + " 的信息");
                return CommonResult.fail("查询失败：部门不存在或不属于当前组织");
            } else if (!Objects.equals(department.getOrganizationId(), context.getOrganizationId())){
                log.info("B 端用户 " + context + " 尝试访问不属于已登录组织的部门 " + department + " 的信息");
                return CommonResult.fail("查询失败：部门不存在或不属于当前组织");
            }
        }
        // 数据库查询到的数据
        List<LineChartLineDataDaoPo> daoDatas = interviewBoardBDataMapper.selectInterviewNumberLineChartGroupByDataByDepartmentIdIdAndAdmissionId(
                departmentId, admission.getId());
        GetNumberGroupByTimeAndOrderResult result = new GetNumberGroupByTimeAndOrderResult();
        List<LineChartLineDataPo> orderDates = new LinkedList<>();
        if (daoDatas.size() == 0){
            result.setOrders(orderDates);
            result.setDate(new ArrayList<>());
            return CommonResult.success(result);
        }
        result.setDate(daoDatas.get(0).getAbscissaData());
        for (LineChartLineDataDaoPo data : daoDatas){
            LineChartLineDataPo depData = new LineChartLineDataPo();
            depData.setName(data.getName());
            depData.setData(data.getYDatas());
            orderDates.add(depData);
        }
        result.setOrders(orderDates);
        return CommonResult.success(result);
    }
}

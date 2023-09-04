package com.sipc.mmtbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sipc.mmtbackend.mapper.DepartmentMapper;
import com.sipc.mmtbackend.mapper.InterviewBoardDataMapper;
import com.sipc.mmtbackend.mapper.InterviewCheckMapper;
import com.sipc.mmtbackend.pojo.domain.Admission;
import com.sipc.mmtbackend.pojo.domain.Department;
import com.sipc.mmtbackend.pojo.domain.InterviewStatus;
import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardResult.GetDepartmentsResult;
import com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardResult.GetIntreviewStatusResult;
import com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardResult.po.GetDepartmentPo;
import com.sipc.mmtbackend.service.InterviewBoardService;
import com.sipc.mmtbackend.utils.CheckroleBUtil.pojo.BTokenSwapPo;
import com.sipc.mmtbackend.utils.ThreadLocalContextUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Slf4j
public class InterviewBoardServiceImpl implements InterviewBoardService {
    private final DepartmentMapper departmentMapper;
    private final InterviewCheckMapper interviewCheckMapper;
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
        List<GetDepartmentPo> departments = new ArrayList<>(departmentList.size());
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
     * 获取当前纳新状态
     *
     * @return 当前纳新状态 0未开始，1正在进行，2已结束
     */
    @Override
    public CommonResult<GetIntreviewStatusResult> getInterviewStatus() {
        BTokenSwapPo context = ThreadLocalContextUtil.getContext();
        Admission admission = interviewCheckMapper.selectOrganizationActivateAdmission(context.getOrganizationId());
        // 无活动纳新
        if (admission == null) {
            log.warn("用户 " + context + " 尝试在无活动的纳新时查询纳新状态");
            return CommonResult.fail("查询失败：未开始纳新或纳新已结束");
        }
        log.info("用户 " + context + " 开始判断纳新 " + admission + " 的状态\n");
        GetIntreviewStatusResult result = new GetIntreviewStatusResult();
        Integer maxRound = interviewCheckMapper.selectOrganizationActivateInterviewRound(admission.getId());
        // 无活动的面试
        if (maxRound == null){
            log.info("未安排面试\n");
            result.setStatus(0);
            return CommonResult.success(result);
        }
        log.info("\t当前面试轮次为第 " + maxRound + " 轮\n");
//        Integer checkinCount = interviewBoardDataMapper.selectCountOfCheckinInterview(admission.getId());
//        // 无人签到，面试中
//        if (checkinCount == 0){
//            log.info("第一轮已签到人数为0，面试未开始\n");
//            result.setStatus(0);
//            return CommonResult.success(result);
//        }
//        log.info("\t第一轮已签到人数：" + checkinCount + "\n");
        // 最新一轮不是计划的最后一轮面试
        if (maxRound < admission.getRounds()){
            log.info("最后一轮未开始，面试未结束\n");
            result.setStatus(1);
            return CommonResult.success(result);
        }
        log.info("\t最后一轮（第 " + maxRound + " 轮）已开始\n");
        List<InterviewStatus> notFinishedInterview = interviewBoardDataMapper.selectNotFinishedInterview(
                admission.getId(), maxRound);
        int notFinishedCount = notFinishedInterview.size();
        // 最后一轮面试存在状态不是“待定”的面试
        if (notFinishedCount == 0){
            log.info("最后一轮面试已全部结束\n");
            result.setStatus(2);
            return CommonResult.success(result);
        }
        log.info("\t最后一轮面试未结束\n");
        InterviewStatus last = notFinishedInterview.get(0);
        // 最后一轮未手动结束面试的面试结束时间已过
        if (last.getEndTime().isBefore(LocalDateTime.now())){
            log.warn("最后一轮面试最后一场结束时间已过，现在是" + LocalDateTime.now() + "\n");
            for (InterviewStatus i : notFinishedInterview) {
                log.warn("\t面试 " + i + " 已超过其面试结束时间 " + i.getEndTime() + " 但未结束\n");
            }
            result.setStatus(2);
            return CommonResult.success(result);
        }
        result.setStatus(1);
        return CommonResult.success(result);
    }

}

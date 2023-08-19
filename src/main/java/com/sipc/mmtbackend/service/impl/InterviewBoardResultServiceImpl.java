package com.sipc.mmtbackend.service.impl;

import com.sipc.mmtbackend.mapper.DepartmentMapper;
import com.sipc.mmtbackend.mapper.InterviewBoardRDataMapper;
import com.sipc.mmtbackend.mapper.InterviewCheckMapper;
import com.sipc.mmtbackend.pojo.domain.Admission;
import com.sipc.mmtbackend.pojo.domain.Department;
import com.sipc.mmtbackend.pojo.domain.po.InterviewBoardRPo.InterviewResultData;
import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardResultResult.GetInterviewResultDataResult;
import com.sipc.mmtbackend.service.InterviewBoardResultService;
import com.sipc.mmtbackend.utils.CheckroleBUtil.pojo.BTokenSwapPo;
import com.sipc.mmtbackend.utils.ThreadLocalContextUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Slf4j
public class InterviewBoardResultServiceImpl implements InterviewBoardResultService {
    private final InterviewBoardRDataMapper interviewBoardRDataMapper;
    private final InterviewCheckMapper interviewCheckMapper;
    private final DepartmentMapper departmentMapper;

    /**
     * 获取面试最终数据
     *
     * @param departmentId 部门ID
     * @return 面试最终数据
     */
    @Override
    public CommonResult<GetInterviewResultDataResult> getResultData(int departmentId) {
        BTokenSwapPo context = ThreadLocalContextUtil.getContext();
        Admission admission = interviewCheckMapper.selectOrganizationActivateAdmission(context.getOrganizationId());
        if (admission == null) {
            log.warn("用户 " + context + " 尝试在无活动的纳新时查询面试最终数据");
            return CommonResult.fail("查询失败：未开始纳新或纳新已结束");
        }
        Integer maxRound = interviewCheckMapper.selectOrganizationActivateInterviewRound(admission.getId());
        if (maxRound == null){
            log.warn("用户 " + context + " 在纳新 " + admission + " 中未查询到任何面试");
            return CommonResult.fail("当前纳新未开启面试");
        }
        if (departmentId != 0){
            Department department = departmentMapper.selectById(departmentId);
            if (department == null){
                log.warn("用户 " + context + " 在纳新 " + admission + " 请求不存在的部门" + departmentId + "的面试最终数据");
                return CommonResult.fail("部门不存在或不属于当前组织");
            } else if (!Objects.equals(department.getOrganizationId(), context.getOrganizationId())){
                log.warn("用户 " + context + " 在纳新 " + admission + " 请求不属于其组织的的部门" + department + "的面试最终数据");
                return CommonResult.fail("部门不存在或不属于当前组织");
            }
        }
        GetInterviewResultDataResult result = new GetInterviewResultDataResult();
        InterviewResultData interviewResultData = interviewBoardRDataMapper.selectInterviewResultData(maxRound, admission.getId(), departmentId);
        result.setCheckinCount(interviewResultData.getCheckinCount());
        result.setPassedCount(interviewResultData.getPassedCount());
        result.setSignupCount(interviewResultData.getSignupCount());
        result.setLastPassedCount(interviewResultData.getLastPassedCount());
        return CommonResult.success(result);
    }
}

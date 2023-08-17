package com.sipc.mmtbackend.service.impl;

import com.sipc.mmtbackend.mapper.DepartmentMapper;
import com.sipc.mmtbackend.mapper.InterviewBoardMDataMapper;
import com.sipc.mmtbackend.mapper.InterviewCheckMapper;
import com.sipc.mmtbackend.pojo.domain.Admission;
import com.sipc.mmtbackend.pojo.domain.Department;
import com.sipc.mmtbackend.pojo.domain.po.InterviewBoardMPo.CheckinInfoLPo;
import com.sipc.mmtbackend.pojo.domain.po.InterviewBoardMPo.InterviewProgressPo;
import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.pojo.dto.enums.InterviewRoundEnum;
import com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardMiddleResult.GetCheckinListResult;
import com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardMiddleResult.GetInterviewProgressCircleResult;
import com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardMiddleResult.GetInterviewRoundsResult;
import com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardMiddleResult.po.CheckinInfoPo;
import com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardMiddleResult.po.InterviewRoomProgressPo;
import com.sipc.mmtbackend.pojo.dto.result.po.KVPo;
import com.sipc.mmtbackend.service.InterviewBoardMiddleService;
import com.sipc.mmtbackend.utils.CheckroleBUtil.pojo.BTokenSwapPo;
import com.sipc.mmtbackend.utils.ThreadLocalContextUtil;
import com.sipc.mmtbackend.utils.TimeTransUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Slf4j
public class InterviewBoardMiddleServiceImpl implements InterviewBoardMiddleService {
    private final InterviewCheckMapper interviewCheckMapper;
    private final DepartmentMapper departmentMapper;
    private final InterviewBoardMDataMapper interviewBoardMDataMapper;

    /**
     * 获取当前组织已开始的面试轮次
     *
     * @return 面试轮次
     */
    @Override
    public CommonResult<GetInterviewRoundsResult> getInterviewRounds() {
        BTokenSwapPo context = ThreadLocalContextUtil.getContext();
        Admission admission = interviewCheckMapper.selectOrganizationActivateAdmission(context.getOrganizationId());
        if (admission == null) {
            log.warn("用户 " + context + " 尝试在无活动的纳新时查询已开始面试轮次");
            return CommonResult.fail("查询失败：未开始纳新或纳新已结束");
        }
        Integer maxRound = interviewCheckMapper.selectOrganizationActivateInterviewRound(admission.getId());
        if (maxRound == null){
            log.warn("用户 " + context + " 在纳新 " + admission + " 中未查询到任何面试");
            return CommonResult.fail("当前纳新未开启面试");
        }
        GetInterviewRoundsResult result = new GetInterviewRoundsResult();
        result.setCount(maxRound);
        List<KVPo> results = new ArrayList<>();
        for (int i = 1; i <= maxRound; i++){
            InterviewRoundEnum round = InterviewRoundEnum.checkRound(i);
            if (round == null){
                log.warn("用户 " + context + " 在纳新 " + admission + " 中查询到非法面试轮次" + i);
                return CommonResult.serverError();
            }
            results.add(new KVPo(i, round.getName()));
        }
        result.setRounds(results);
        return CommonResult.success(result);
    }

    /**
     * 获取已签到人员列表
     *
     * @param round        面试轮次，默认为1
     * @param departmentId 部门 ID，默认0（全部部门）
     * @return 签到信息
     */
    @Override
    public CommonResult<GetCheckinListResult> getCheckinList(int round, int departmentId) {
        BTokenSwapPo context = ThreadLocalContextUtil.getContext();
        Admission admission = interviewCheckMapper.selectOrganizationActivateAdmission(context.getOrganizationId());
        if (admission == null) {
            log.warn("用户 " + context + " 尝试在无活动的纳新时查询已开始面试轮次");
            return CommonResult.fail("查询失败：未开始纳新或纳新已结束");
        }
        Integer maxRound = interviewCheckMapper.selectOrganizationActivateInterviewRound(admission.getId());
        if (maxRound == null){
            log.warn("用户 " + context + " 在纳新 " + admission + " 中未查询到任何面试");
            return CommonResult.fail("当前纳新未开启面试");
        }
        if (round > maxRound){
            log.warn("用户 " + context + " 在纳新 " + admission + " 请求不存在的面试轮次 " + round + "/" + maxRound);
            return CommonResult.fail("指定的面试轮次还未开始");
        }
        if (departmentId != 0){
            Department department = departmentMapper.selectById(departmentId);
            if (department == null){
                log.warn("用户 " + context + " 在纳新 " + admission + " 请求不存在的部门" + departmentId + "的签到列表");
                return CommonResult.fail("部门不存在或不属于当前组织");
            } else if (!Objects.equals(department.getOrganizationId(), context.getOrganizationId())){
                log.warn("用户 " + context + " 在纳新 " + admission + " 请求不属于其组织的的部门" + department + "的签到列表");
                return CommonResult.fail("部门不存在或不属于当前组织");
            }
        }
        List<CheckinInfoLPo> checkinInfos = interviewBoardMDataMapper.selectCheckinInfo(round, admission.getId(), departmentId);
        GetCheckinListResult result = new GetCheckinListResult();
        List<CheckinInfoPo> results = new ArrayList<>();
        for (CheckinInfoLPo info : checkinInfos) {
            CheckinInfoPo po = new CheckinInfoPo();
            po.setName(info.getName());
            po.setRoom(info.getRoom());
            po.setStudentId(info.getStudentId());
            po.setTime(TimeTransUtil.transStringToTimeDataDashboard(info.getTime()));
            results.add(po);
        }
        result.setCount(checkinInfos.size());
        result.setCheckins(results);
        return CommonResult.success(result);
    }

    /**
     * 获取面试进度
     *
     * @param round        面试轮次，默认为1
     * @param departmentId 部门 ID，默认0（全部部门）
     * @return 各个面试地点的面试进度
     */
    @Override
    public CommonResult<GetInterviewProgressCircleResult> getInterviewProgressCircle(int round, int departmentId) {
        BTokenSwapPo context = ThreadLocalContextUtil.getContext();
        Admission admission = interviewCheckMapper.selectOrganizationActivateAdmission(context.getOrganizationId());
        if (admission == null) {
            log.warn("用户 " + context + " 尝试在无活动的纳新时查询已开始面试轮次");
            return CommonResult.fail("查询失败：未开始纳新或纳新已结束");
        }
        Integer maxRound = interviewCheckMapper.selectOrganizationActivateInterviewRound(admission.getId());
        if (maxRound == null){
            log.warn("用户 " + context + " 在纳新 " + admission + " 中未查询到任何面试");
            return CommonResult.fail("当前纳新未开启面试");
        }
        if (round > maxRound){
            log.warn("用户 " + context + " 在纳新 " + admission + " 请求不存在的面试轮次 " + round + "/" + maxRound);
            return CommonResult.fail("指定的面试轮次还未开始");
        }
        if (departmentId != 0){
            Department department = departmentMapper.selectById(departmentId);
            if (department == null){
                log.warn("用户 " + context + " 在纳新 " + admission + " 请求不存在的部门" + departmentId + "的签到列表");
                return CommonResult.fail("部门不存在或不属于当前组织");
            } else if (!Objects.equals(department.getOrganizationId(), context.getOrganizationId())){
                log.warn("用户 " + context + " 在纳新 " + admission + " 请求不属于其组织的的部门" + department + "的签到列表");
                return CommonResult.fail("部门不存在或不属于当前组织");
            }
        }
        List<InterviewProgressPo> progress = interviewBoardMDataMapper.selectInterviewProgress(round, admission.getId(), departmentId);
        GetInterviewProgressCircleResult result = new GetInterviewProgressCircleResult();
        List<InterviewRoomProgressPo> results = new ArrayList<>();
        for (InterviewProgressPo ipp : progress) {
            InterviewRoomProgressPo irpp = new InterviewRoomProgressPo();
            irpp.setName(ipp.getName());
            irpp.setTotal(ipp.getTotal());
            irpp.setFinished(ipp.getFinished());
            results.add(irpp);
        }
        result.setRooms(results);
        result.setCount(results.size());
        return CommonResult.success(result);
    }
}

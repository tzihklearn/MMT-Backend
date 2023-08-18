package com.sipc.mmtbackend.service.impl;

import com.sipc.mmtbackend.mapper.InterviewCheckMapper;
import com.sipc.mmtbackend.mapper.InterviewStatusMapper;
import com.sipc.mmtbackend.mapper.RealtimeInterviewMapper;
import com.sipc.mmtbackend.pojo.domain.Admission;
import com.sipc.mmtbackend.pojo.domain.AdmissionAddress;
import com.sipc.mmtbackend.pojo.domain.InterviewStatus;
import com.sipc.mmtbackend.pojo.domain.po.RealtimeInterviewPo.ProgressBarPo;
import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.pojo.dto.param.RealtimeInterview.FinishInterviewParam;
import com.sipc.mmtbackend.pojo.dto.result.RealtimeIntreviewdResult.GetInterviewPlacesResult;
import com.sipc.mmtbackend.pojo.dto.result.RealtimeIntreviewdResult.GetInterviewProgressBarResult;
import com.sipc.mmtbackend.pojo.dto.result.RealtimeIntreviewdResult.po.ProgressBarDataPo;
import com.sipc.mmtbackend.pojo.dto.result.po.KVPo;
import com.sipc.mmtbackend.service.RealtimeInterviewService;
import com.sipc.mmtbackend.utils.CheckinQRCodeUtil.CheckinQRCodeUtil;
import com.sipc.mmtbackend.utils.CheckroleBUtil.pojo.BTokenSwapPo;
import com.sipc.mmtbackend.utils.ThreadLocalContextUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Slf4j
public class RealtimeInterviewServiceImpl implements RealtimeInterviewService {
    private final InterviewCheckMapper interviewCheckMapper;
    private final RealtimeInterviewMapper realtimeInterviewMapper;
    private final CheckinQRCodeUtil checkinQRCodeUtil;
    private final InterviewStatusMapper interviewStatusMapper;

    /**
     * 获取签到二维码
     *
     * @return 签到二维码的 Base64 编码
     */
    @Override
    public CommonResult<String> getCheckinQRCode() {
        BTokenSwapPo context = ThreadLocalContextUtil.getContext();
        Admission admission = interviewCheckMapper.selectOrganizationActivateAdmission(context.getOrganizationId());
        if (admission == null) {
            log.warn("用户 " + context + " 尝试在无活动的纳新时生成签到二维码");
            return CommonResult.fail("生成失败：未开始纳新或纳新已结束");
        }
        String qrcode = checkinQRCodeUtil.getCheckinQRCode(context.getOrganizationId(), context.getUserId());
        if (qrcode == null){
            log.warn("User: " + context + " Create Checkin QR Code Error\n");
            return CommonResult.serverError();
        }
        CommonResult<String> result = CommonResult.success();
        result.setData(qrcode);
        return result;
    }

    /**
     * 获取面试场地
     *
     * @return 所有面试场地
     */
    @Override
    public CommonResult<GetInterviewPlacesResult> getInterviewPlaces() {
        BTokenSwapPo context = ThreadLocalContextUtil.getContext();
        Admission admission = interviewCheckMapper.selectOrganizationActivateAdmission(context.getOrganizationId());
        if (admission == null) {
            log.warn("用户 " + context + " 尝试在无活动的纳新时获取面试场地");
            return CommonResult.fail("生成失败：未开始纳新或纳新已结束");
        }
        Integer maxRound = interviewCheckMapper.selectOrganizationActivateInterviewRound(admission.getId());
        if (maxRound == null){
            log.warn("用户 " + context + " 在纳新 " + admission + " 中未查询到任何面试");
            return CommonResult.fail("当前纳新未开启面试");
        }
        List<AdmissionAddress> addresses = interviewCheckMapper.selectAvaliableInterviewAddress(maxRound, admission.getId(), 0);
        GetInterviewPlacesResult result = new GetInterviewPlacesResult();
        result.setCount(addresses.size());
        List<KVPo> results = new ArrayList<>();
        for (AdmissionAddress address : addresses) {
            results.add(new KVPo(address.getId(), address.getName()));
        }
        result.setPlaces(results);
        return CommonResult.success(result);
    }

    /**
     * 获取面试进度条
     *
     * @param placeId 面试场地 ID
     * @return 面试进度条数据
     */
    @Override
    public CommonResult<GetInterviewProgressBarResult> getInterviewProgressBar(int placeId) {
        BTokenSwapPo context = ThreadLocalContextUtil.getContext();
        Admission admission = interviewCheckMapper.selectOrganizationActivateAdmission(context.getOrganizationId());
        if (admission == null) {
            log.warn("用户 " + context + " 尝试在无活动的纳新时获取面试进度条");
            return CommonResult.fail("生成失败：未开始纳新或纳新已结束");
        }
        Integer maxRound = interviewCheckMapper.selectOrganizationActivateInterviewRound(admission.getId());
        if (maxRound == null){
            log.warn("用户 " + context + " 在纳新 " + admission + " 中未查询到任何面试");
            return CommonResult.fail("当前纳新未开启面试");
        }
        List<ProgressBarPo> progressBarPos = realtimeInterviewMapper.selectInterviewProgress(maxRound, admission.getId(), placeId);
        GetInterviewProgressBarResult result = new GetInterviewProgressBarResult();
        List<ProgressBarDataPo> results = new ArrayList<>();
        for (ProgressBarPo pbp : progressBarPos) {
            ProgressBarDataPo pbdp = new ProgressBarDataPo();
            pbdp.setFinished(pbp.getFinished());
            pbdp.setTime(pbp.getHour() + ":00-" + (pbp.getHour() + 1) + ":00");
            pbdp.setTotal(pbp.getTotal());
            results.add(pbdp);
        }
        result.setBars(results);
        result.setGroupNum(results.size());
        return CommonResult.success(result);
    }

    /**
     * 结束面试
     *
     * @param param 面试ID
     * @return 处理结果
     */
    @Override
    public CommonResult<String> finishInterview(FinishInterviewParam param) {
        BTokenSwapPo context = ThreadLocalContextUtil.getContext();
        Admission admission = interviewCheckMapper.selectOrganizationActivateAdmission(context.getOrganizationId());
        if (admission == null) {
            log.warn("用户 " + context + " 尝试在无活动的纳新时获取面试进度条");
            return CommonResult.fail("生成失败：未开始纳新或纳新已结束");
        }
        Integer maxRound = interviewCheckMapper.selectOrganizationActivateInterviewRound(admission.getId());
        if (maxRound == null){
            log.warn("用户 " + context + " 在纳新 " + admission + " 中未查询到任何面试");
            return CommonResult.fail("当前纳新未开启面试");
        }
        InterviewStatus interviewStatus = interviewStatusMapper.selectById(param.getInterviewId());
        if (interviewStatus == null){
            log.warn("用户 " + context + " 尝试结束不存在的面试 " + param + "\n");
            return CommonResult.fail("面试不存在或不属于当前纳新");
        } else if (!Objects.equals(interviewStatus.getAdmissionId(), admission.getId())){
            log.warn("用户 " + context + " 尝试结束不属于当前纳新 " + admission + " 的面试 " + interviewStatus + "\n");
            return CommonResult.fail("面试不存在或不属于当前纳新");
        }
        if (interviewStatus.getState() != 6){
            log.warn("用户 " + context +" 尝试结束并非正在进行的面试 " + interviewStatus + "\n");
            if (interviewStatus.getState() == 7){
                return CommonResult.success("面试已结束，请勿重复请求");
            } else {
                return CommonResult.fail("面试未开始或已结束");
            }
        }
        interviewStatus.setState(7);
        int update = interviewStatusMapper.updateById(interviewStatus);
        if (update != 1){
            log.warn("结束面试失败: " + interviewStatus + " ,受影响的行数: " + update + "\n");
            return CommonResult.serverError();
        }
        return CommonResult.success();
    }
}

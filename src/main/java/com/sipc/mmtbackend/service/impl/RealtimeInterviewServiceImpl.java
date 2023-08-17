package com.sipc.mmtbackend.service.impl;

import com.sipc.mmtbackend.mapper.InterviewCheckMapper;
import com.sipc.mmtbackend.pojo.domain.Admission;
import com.sipc.mmtbackend.pojo.domain.AdmissionAddress;
import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.pojo.dto.result.RealtimeIntreviewdResult.GetInterviewPlacesResult;
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

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Slf4j
public class RealtimeInterviewServiceImpl implements RealtimeInterviewService {
    private final InterviewCheckMapper interviewCheckMapper;
    private final CheckinQRCodeUtil checkinQRCodeUtil;

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
}

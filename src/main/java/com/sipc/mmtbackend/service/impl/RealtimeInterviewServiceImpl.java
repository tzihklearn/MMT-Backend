package com.sipc.mmtbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sipc.mmtbackend.mapper.AdmissionMapper;
import com.sipc.mmtbackend.pojo.domain.Admission;
import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.service.RealtimeInterviewService;
import com.sipc.mmtbackend.utils.CheckinQRCodeUtil.CheckInQRCodeUtil;
import com.sipc.mmtbackend.utils.CheckroleBUtil.pojo.BTokenSwapPo;
import com.sipc.mmtbackend.utils.ThreadLocalContextUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Slf4j
public class RealtimeInterviewServiceImpl implements RealtimeInterviewService {
    private final AdmissionMapper admissionMapper;
    private final CheckInQRCodeUtil checkInQRCodeUtil;

    /**
     * 获取签到二维码
     *
     * @return 签到二维码的 Base64 编码
     */
    @Override
    public CommonResult<String> getCheckInQRCode() {
        BTokenSwapPo context = ThreadLocalContextUtil.getContext();
        Admission admission = admissionMapper.selectOne(
                new QueryWrapper<Admission>()
                        .eq("organization_id", context.getOrganizationId())
                        .orderByDesc("id"));
        if (admission == null) {
            log.warn("用户 " + context + " 尝试在无活动的纳新时生成签到二维码");
            return CommonResult.fail("生成失败：未开始纳新或纳新已结束");
        }
        String qrcode = checkInQRCodeUtil.getCheckinQRCode(context.getOrganizationId(), context.getUserId());
        if (qrcode == null){
            log.warn("User: " + context + " Create Checkin QR Code Error\n");
            return CommonResult.serverError();
        }
        CommonResult<String> result = CommonResult.success();
        result.setData(qrcode);
        return result;
    }
}

package com.sipc.mmtbackend.service.c.impl;

import com.sipc.mmtbackend.controller.c.UpdateUserInfoController;
import com.sipc.mmtbackend.mapper.OrganizationMapper;
import com.sipc.mmtbackend.mapper.c.CheckInMapper;
import com.sipc.mmtbackend.pojo.c.param.InterviewCheckInParam;
import com.sipc.mmtbackend.pojo.c.param.IsCertificationParam;
import com.sipc.mmtbackend.pojo.domain.Organization;
import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.pojo.dto.resultEnum.ResultEnum;
import com.sipc.mmtbackend.service.c.InterviewCheckInService;
import com.sipc.mmtbackend.utils.CheckinQRCodeUtil.CheckinQRCodeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Slf4j
public class InterviewCheckInServiceImpl implements InterviewCheckInService {
    private final UpdateUserInfoController updateUserInfoController;
    private final CheckinQRCodeUtil checkinQRCodeUtil;
    private final OrganizationMapper organizationMapper;
    private final CheckInMapper checkInMapper;
    /**
     * C 端签到
     *
     * @param request
     * @param response
     * @param param    二维码载荷
     * @return 签到结构
     */

    @Override
    public CommonResult interviewCheckin(HttpServletRequest request, HttpServletResponse response, InterviewCheckInParam param) {
        // 验证登录状态，获取 userID
        CommonResult<IsCertificationParam> isCertificationParamCommonResult = updateUserInfoController.updateUserInfo(request, response);
        if (!Objects.equals(isCertificationParamCommonResult.getCode(), ResultEnum.SUCCESS.getCode()))
            return isCertificationParamCommonResult;
        if (!isCertificationParamCommonResult.getData().getIs_certification())
            return CommonResult.fail("A0140", "用户未认证");
        Integer userId = isCertificationParamCommonResult.getData().getUserId();
        Integer orgId = checkinQRCodeUtil.verifyQRCode(param.getKey());
        if (orgId == null){
            log.warn("C 端用户 " + userId + "扫描了无效的二维码");
            return CommonResult.fail("二维码无效");
        }
        Organization organization = organizationMapper.selectById(orgId);
        if (organization == null){
            log.warn("C 端用户 " + userId + "扫描了无效的二维码，其组织 " + orgId + " 不存在");
            return CommonResult.fail("二维码无效");
        }
        checkInMapper.checkInUser(userId, orgId);
        return CommonResult.success();
    }
}

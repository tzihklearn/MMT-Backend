package com.sipc.mmtbackend.service.c.impl;

import com.sipc.mmtbackend.common.Constant;
import com.sipc.mmtbackend.mapper.UserInfoMapper;
import com.sipc.mmtbackend.mapper.c.UserCMapper;
import com.sipc.mmtbackend.pojo.c.param.IsCertificationParam;
import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.service.c.UpdateUserInfoService;
import com.sipc.mmtbackend.utils.checkRoleUtils.CheckRole;
import com.sipc.mmtbackend.utils.checkRoleUtils.param.CheckResultParam;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

@Service
public class UpdateUserInfoServiceImpl implements UpdateUserInfoService {
    @Resource
    UserInfoMapper userInfoMapper;
    @Resource
    UserCMapper userCMapper;

    @Override
    public synchronized CommonResult<IsCertificationParam> updateUserInfo(HttpServletRequest request, HttpServletResponse response) {
        CheckResultParam check = CheckRole.check(request, response, Constant.C_END);
        if (!check.isResult()) {
            return CommonResult.fail(check.getErrcode(), check.getErrmsg());
        }
        String openId;
        try {
            openId = check.getData();

        } catch (NullPointerException e) {
            return CommonResult.fail("Cookie失效");
        }
        Integer userId = userCMapper.selectIdByOpenId(openId);// 用 openid 获得 userId
        if (userId == null) return CommonResult.fail("查无此人");
        IsCertificationParam isCertificationParam = userInfoMapper.selectCertificationByUserId(userId);
        if (Objects.isNull(isCertificationParam)) return CommonResult.fail("请完善个人信息");
        Integer isCertification = userInfoMapper.selectIsCertification(userId);
        if (Objects.equals(isCertificationParam.getGender(), "1")) isCertificationParam.setGender("男");
        else isCertificationParam.setGender("女");
        String major = isCertificationParam.getMajor();
        String classNum = isCertificationParam.getClassNum();
        isCertificationParam.setClassNum(major + classNum);
        isCertificationParam.setUserId(userId);
        isCertificationParam.setIs_certification(isCertification == 1);

        return CommonResult.success(isCertificationParam);
    }
}

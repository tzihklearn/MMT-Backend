package com.sipc.mmtbackend.service.c.impl;

import cn.hutool.core.codec.Base64Encoder;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sipc.mmtbackend.common.Constant;
import com.sipc.mmtbackend.mapper.*;
import com.sipc.mmtbackend.mapper.c.UserCMapper;
import com.sipc.mmtbackend.pojo.c.domain.UserC;
import com.sipc.mmtbackend.pojo.c.param.SecondUpdateParam;
import com.sipc.mmtbackend.pojo.c.result.PersonalInfoArrangeBackResult;
import com.sipc.mmtbackend.pojo.c.result.PersonalInfoArrangeResult;
import com.sipc.mmtbackend.pojo.c.result.UserInfoResult;
import com.sipc.mmtbackend.pojo.domain.*;
import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.service.c.PersonalInfoService;
import com.sipc.mmtbackend.utils.PictureUtil.PictureUtil;
import com.sipc.mmtbackend.utils.PictureUtil.pojo.DefaultPictureIdEnum;
import com.sipc.mmtbackend.utils.checkRoleUtils.CheckRole;
import com.sipc.mmtbackend.utils.checkRoleUtils.param.CheckResultParam;
import com.sipc.mmtbackend.utils.urlBase64.UrlBase64;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

@Service
public class PersonalInfoServiceImpl implements PersonalInfoService {
    @Resource
    UserInfoMapper userInfoMapper;
    @Resource
    UserCMapper userCMapper;
    @Resource
    InterviewStatusMapper interviewStatusMapper;
    @Resource
    AdmissionAddressMapper admissionAddressMapper;
    @Resource
    DepartmentMapper departmentMapper;
    @Resource
    OrganizationMapper organizationMapper;

    @Resource
    AcaMajorMapper acaMajorMapper;

    @Resource
    MajorClassMapper majorClassMapper;

    @Resource
    PictureUtil pictureUtil;

    @Override
    public synchronized CommonResult<UserInfoResult> getPersonalInfo(HttpServletRequest request, HttpServletResponse response) {
        CheckResultParam check = CheckRole.check(request, response, Constant.C_END);
        String openId = check.getData();
        if (openId == null) return CommonResult.fail("Cookie失效");

        UserC user = userCMapper.selectByOpenid(openId);
        Integer studentId = user.getStudentId();

        if (userInfoMapper.selectStudentIdForInject(studentId) == 0) return CommonResult.fail("无相关信息");

        UserInfoResult userInfoResult;
        try {
            userInfoResult = userInfoMapper.selectAllByStudentId(studentId);
            if (userInfoResult.getGender().equals("1")) userInfoResult.setGender("男");
            else userInfoResult.setGender("女");
        } catch (NullPointerException nullPointerException) {
            return CommonResult.fail("信息不全");
        }
        return CommonResult.success(userInfoResult);
    }

    @Override
    public synchronized CommonResult<String> updatePersonalInfo(HttpServletRequest request, HttpServletResponse response, SecondUpdateParam secondUpdateParam) {
        CheckResultParam check = CheckRole.check(request, response, Constant.C_END);
        String openId = check.getData();
        if (openId == null) return CommonResult.fail("Cookie失效");

        UserC user = userCMapper.selectByOpenid(openId);
        Integer studentId = user.getStudentId();

        String phoneNum = secondUpdateParam.getPhoneNum();
        String email = secondUpdateParam.getEmail();
        String qqNum = secondUpdateParam.getQqNum();
        try {
            UserInfo userInfoDomain = userInfoMapper.selectUserIdByStudentId(studentId);
            Integer userId = userInfoDomain.getId();
            UserInfo userInfo = userInfoMapper.selectById(userId);
            if (Objects.isNull(userId)) return CommonResult.fail("无该学生");
            if (Objects.isNull(userInfo.getStudentId()) || (Objects.isNull(userInfo.getPhone())) || Objects.isNull(userInfo.getQq()))
                return CommonResult.fail("该学生尚未填写被修改的信息");
        } catch (NullPointerException nullPointerException) {
            return CommonResult.fail("无该学生");
        }

        UserC temp = new UserC();
        temp.setId(user.getId());
        temp.setPhone(secondUpdateParam.getPhoneNum());

        Boolean flag = userInfoMapper.updateSecondInfo(phoneNum, email, qqNum, studentId);
        userCMapper.updateByPrimaryKey(temp);
        if (!flag) return CommonResult.fail("更新失败");

        return CommonResult.success("更新成功");
    }

    @Override
    public synchronized CommonResult<String> insertPersonalInfo(HttpServletRequest request, HttpServletResponse response, UserInfoResult userInfoResult) {
        CheckResultParam check = CheckRole.check(request, response, Constant.C_END);
        String openId = check.getData();
        if (openId == null) return CommonResult.fail("Cookie失效");

        Integer studentId = userInfoResult.getStudentId();
        UserInfo userInfo = userInfoMapper.selectByStudentId(studentId);
        /**
         * 汤子涵改动
         */
        UserC user1 = userCMapper.selectByStudentId(studentId);
        if (!Objects.isNull(userInfo) || !Objects.isNull(user1) ) return CommonResult.fail("信息已存在");

        String name = userInfoResult.getName();
        String academy = userInfoResult.getAcademy();
        String major = userInfoResult.getMajor();
        String classNum = userInfoResult.getClassNum();
        String phoneNum = userInfoResult.getPhoneNum();
        String email = userInfoResult.getEmail();
        String qqNum = userInfoResult.getQqNum();
        String gender = userInfoResult.getGender();

        Integer userId;
        try {
            UserC user = userCMapper.selectByOpenid(openId);
            if (Objects.isNull(user)) userCMapper.insertAll(studentId, name, phoneNum, openId);
            userId = userCMapper.selectIdByOpenId(openId);
        } catch (TooManyResultsException e) {
            return CommonResult.fail("数据库信息重复");
        }

        if (gender.equals("男")) gender = String.valueOf(1);
        if (gender.equals("女")) gender = String.valueOf(0);
        try {

            AcaMajor acaMajor = acaMajorMapper.selectOne(
                    new QueryWrapper<AcaMajor>()
                            .eq("academy", academy)
                            .eq("major", major)
                            .last("limit 1")
            );

            MajorClass majorClass = majorClassMapper.selectOne(
                    new QueryWrapper<MajorClass>()
                            .eq("major_id", acaMajor.getId())
                            .eq("class_num", classNum)
                            .last("limit 1")
            );

//            boolean ret = userInfoMapper.updateAll(userId, name, studentId, Integer.valueOf(gender), academy, major, classNum, phoneNum, email, qqNum, 1);

            boolean ret = userInfoMapper.updateAll(userId, name, studentId, Integer.valueOf(gender), acaMajor.getId(), majorClass.getId(), classNum, phoneNum, email, qqNum, 1);
            //更新User表 yuleng
            UserC temp = new UserC();
            temp.setId(userId);
            temp.setName(name);
            temp.setStudentId(studentId);
            temp.setPhone(phoneNum);
            userCMapper.updateByPrimaryKey(temp);
            if (ret) return CommonResult.success("填写成功");
        } catch (Exception e) {
            return CommonResult.fail("openid已被使用");
        }
        return CommonResult.fail("填写失败！");
    }

    @Override
    public CommonResult<PersonalInfoArrangeBackResult> getPersonalArrangeInfo(HttpServletRequest request, HttpServletResponse response) throws IOException {
        CheckResultParam check = CheckRole.check(request, response, Constant.C_END);
        String openId = check.getData();
        if (openId == null) return CommonResult.fail("Cookie失效");

        UserC user = userCMapper.selectByOpenid(openId);
        Integer studentId = user.getStudentId();

        List<PersonalInfoArrangeResult> resultList = new ArrayList<>();

        List<InterviewStatus> interviewStatusList = interviewStatusMapper.selectAllArrange(studentId);
        for (InterviewStatus interviewStatus : interviewStatusList) {
            //2022.8.30 yuleng
            if (interviewStatus == null) return CommonResult.success("暂无面试安排");
            PersonalInfoArrangeResult result = new PersonalInfoArrangeResult();
            Integer admissionAddressId = interviewStatus.getAdmissionAddressId();
            String address = admissionAddressMapper.selectAddressById(admissionAddressId);
            if (Objects.isNull(address)) continue;
            Integer departmentId = interviewStatus.getDepartmentId();
            Department department = departmentMapper.selectById(departmentId);
            if (Objects.isNull(department)) continue;
            String name = department.getName();
            Integer organizationId = departmentMapper.selectOrganizationIdById(departmentId);
            if (Objects.isNull(organizationId)) continue;
            Organization organization = organizationMapper.selectById(organizationId);
            String organizationName;
            String organizationLogoUrl;
            try {
                organizationName = organization.getName();
                if (Objects.isNull(organizationName)) continue;

                String avatarUrl;
//
//                if (organization.getAvatarId() == null || organization.getAvatarId().isEmpty()) {
//                    avatarUrl = pictureUtil.getPictureURL(DefaultPictureIdEnum.ORG_AVATAR.getPictureId(), true);
//                } else {
//                    avatarUrl = pictureUtil.getPictureURL(organization.getAvatarId(), false);
//                }

                //TODO:社团头像临时处理
                avatarUrl = organization.getAvatarId();

                organizationLogoUrl = avatarUrl;
                if (Objects.isNull(organizationLogoUrl)) continue;
            } catch (NullPointerException e) {
                return CommonResult.fail("数据库数据异常");
            }

            byte[] pngByte = UrlBase64.getPng(organizationLogoUrl);
            String encode = Base64Encoder.encode(pngByte);//转为Base64编码
            //TODO:
            Integer status = interviewStatus.getState();
            if (Objects.isNull(status)) continue;
            boolean statusBool;
            statusBool = status >= 6;
            String round = String.valueOf(interviewStatus.getRound());
            if (Objects.isNull(round)) continue;
            int roundFlag = Integer.parseInt(round);
            switch (roundFlag) {
                case 1:
                    round = "一面";
                    break;
                case 2:
                    round = "二面";
                    break;
                case 3:
                    round = "三面";
                    break;
                case 4:
                    round = "四面";
                    break;
                default:
                    return CommonResult.fail("数据库信息错误");
            }
            Long startTime = interviewStatus.getStartTime().toEpochSecond(ZoneOffset.of("+8"));
            if (Objects.isNull(startTime)) continue;

            String pattern = "yyyy-MM-dd HH:mm";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));
            //时间戳格式化模板

            String formatStartTime = simpleDateFormat.format(startTime * 1000);

            result.setDepartmentName(name);
            result.setStartTime(formatStartTime);
            result.setAddress(address);
            result.setRound(round);
            result.setStatus(statusBool);
            result.setOrganizationName(organizationName);
            result.setPng(encode);

            resultList.add(result);
        }
        PersonalInfoArrangeBackResult result = new PersonalInfoArrangeBackResult();
        result.setList(resultList);
        return CommonResult.success(result);
    }

    @Override
    public synchronized CommonResult<String> getUrl(HttpServletRequest request, HttpServletResponse response) throws IOException {
        CheckResultParam check = CheckRole.check(request, response, Constant.C_END);
        String openId = check.getData();
        if (openId == null) return CommonResult.fail("Cookie失效");

        UserC user = userCMapper.selectByOpenid(openId);
        Integer studentId = user.getStudentId();

        UserInfo userInfo;
        String avatarUrl;
        try {
            userInfo = userInfoMapper.selectByStudentId(studentId);
            avatarUrl = userInfo.getAvatarUrl();
        } catch (NullPointerException e) {
            return CommonResult.fail("无头像链接");
        }

        if (Objects.isNull(userInfo.getAvatarUrl())) return CommonResult.fail("无头像链接");
        byte[] png = UrlBase64.getPng(avatarUrl);
        String encode = Base64Encoder.encode(png);

        return CommonResult.success(encode);
    }
}

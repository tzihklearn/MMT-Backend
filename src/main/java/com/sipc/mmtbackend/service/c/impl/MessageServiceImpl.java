package com.sipc.mmtbackend.service.c.impl;

import com.sipc.mmtbackend.controller.c.UpdateUserInfoController;
import com.sipc.mmtbackend.mapper.InterviewStatusMapper;
import com.sipc.mmtbackend.mapper.MessageMapper;
import com.sipc.mmtbackend.mapper.OrganizationMapper;
import com.sipc.mmtbackend.mapper.UserInfoMapper;
import com.sipc.mmtbackend.pojo.c.domain.po.Message.MessagePo;
import com.sipc.mmtbackend.pojo.c.param.IsCertificationParam;
import com.sipc.mmtbackend.pojo.c.param.MessageParam.ReadMessageParam;
import com.sipc.mmtbackend.pojo.c.param.MessageParam.SubmitStateParam;
import com.sipc.mmtbackend.pojo.c.result.MessageResult.GetMessageResult;
import com.sipc.mmtbackend.pojo.c.result.MessageResult.MessageResult;
import com.sipc.mmtbackend.pojo.domain.InterviewStatus;
import com.sipc.mmtbackend.pojo.domain.Message;
import com.sipc.mmtbackend.pojo.domain.Organization;
import com.sipc.mmtbackend.pojo.domain.UserInfo;
import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.pojo.dto.resultEnum.ResultEnum;
import com.sipc.mmtbackend.service.c.MessageService;
import com.sipc.mmtbackend.utils.PictureUtil.PictureUtil;
import com.sipc.mmtbackend.utils.time.TimeUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class MessageServiceImpl implements MessageService {
    @Resource
    UpdateUserInfoController updateUserInfoController;
    @Resource
    MessageMapper messageMapper;

    @Resource
    OrganizationMapper organizationMapper;

    @Resource
    UserInfoMapper userInfoMapper;

    @Resource
    InterviewStatusMapper interviewStatusMapper;
    @Resource
    PictureUtil pictureUtil;

    /**
     * @apiNote 获取消息摘要 C-已认证用户
     */
    @Override
    public CommonResult getAllMessages(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        // 验证登录状态，获取 userID
        CommonResult<IsCertificationParam> isCertificationParamCommonResult = updateUserInfoController.updateUserInfo(httpServletRequest, httpServletResponse);
        if (!Objects.equals(isCertificationParamCommonResult.getCode(), ResultEnum.SUCCESS.getCode()))
            return isCertificationParamCommonResult;
        if (!isCertificationParamCommonResult.getData().getIs_certification())
            return CommonResult.fail("A0140", "用户未认证");
        Integer userId = isCertificationParamCommonResult.getData().getUserId();

        // 响应体data
        ArrayList<MessageResult> results = new ArrayList<>();

        // 获取每个组织的最新消息
        ArrayList<MessagePo> messagePos = messageMapper.selectNewestMessageByUserId(userId);

        for (MessagePo messagePo : messagePos) {

            Integer count = messageMapper.selectCountForUnreadMessage(messagePo.getOrganizationId(), userId);

            MessageResult result = new MessageResult();

            String message = messagePo.getMessage();
            if (message.length() > 15)
                message = message.substring(0, 15);

            //设置属性
            result.setOrganizationId(messagePo.getOrganizationId());
            result.setAvatarUrl(messagePo.getAvatarUrl());
            result.setOrganizationName(messagePo.getName());
            result.setMessage(message);
            result.setUnread(count);
            result.setTime(TimeUtil.EasyRead(messagePo.getTime().toEpochSecond(ZoneOffset.of("+8"))));
            results.add(result);
        }
        return CommonResult.success(results);
    }

    /**
     * @param organizationId 组织ID
     * @apiNote 获取指定组织的消息 C-已认证用户
     */
    @Override
    public CommonResult getMessage(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Integer organizationId) {
        // 验证登录状态，获取 userID
        CommonResult<IsCertificationParam> isCertificationParamCommonResult = updateUserInfoController.updateUserInfo(httpServletRequest, httpServletResponse);
        if (!Objects.equals(isCertificationParamCommonResult.getCode(), ResultEnum.SUCCESS.getCode()))
            return isCertificationParamCommonResult;
        if (!isCertificationParamCommonResult.getData().getIs_certification())
            return CommonResult.fail("A0140", "用户未认证");
        Integer userId = isCertificationParamCommonResult.getData().getUserId();

        // 获取组织头像
        Organization organization = organizationMapper.selectById(organizationId);
        if (Objects.isNull(organization)) {
            return CommonResult.fail("A0420", "组织不存在");
        }

        // 用 userId 获取用户信息
        UserInfo userInfo = userInfoMapper.selectById(userId);

        // 用 user_id 和组织id 查 message 表 拿到 消息内容，时间，消息id
        List<Message> messages = messageMapper.selectMessageByOrganizationIdAndUserId(organization.getId(), userId);

        // 生成响应体 data
        List<GetMessageResult> results = new ArrayList<>();
        for (Message message : messages) {
            GetMessageResult result = new GetMessageResult();
            if (message.getType() == null)
                return CommonResult.serverError();
            switch (message.getType()) {
                case 1:
                case 2:
                    result.setAvatarUrl(
                            pictureUtil.getPictureURL(organization.getAvatarId(),
                            organization.getAvatarId() == null || (organization.getAvatarId().length() == 0)));
                    break;
                case 3:
                    result.setAvatarUrl(userInfo.getAvatarUrl());
                    break;
                default:
                    return CommonResult.serverError();
            }

            result.setType(new Integer(String.valueOf(message.getType())));
            result.setOrganization(organization.getName());
            result.setMessageId(message.getId());
            result.setMessage(message.getMessage());
            result.setTime(TimeUtil.EasyRead(message.getTime().toEpochSecond(ZoneOffset.of("+8"))));
            result.setIsread(message.getIsRead().intValue());
            result.setStatus(message.getState());
            results.add(result);
        }
        return CommonResult.success(results);
    }

    /**
     * @param param 消息ID
     * @apiNote 消息已读 C-已认证用户
     */
    @Override
    public CommonResult<String> readMessage(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, ReadMessageParam param) {
        // 验证登录状态，获取 userID
        CommonResult<IsCertificationParam> isCertificationParamCommonResult = updateUserInfoController.updateUserInfo(httpServletRequest, httpServletResponse);
        if (!Objects.equals(isCertificationParamCommonResult.getCode(), ResultEnum.SUCCESS.getCode())) {
            return CommonResult.fail(isCertificationParamCommonResult.getCode(), isCertificationParamCommonResult.getMessage());
        }
        if (!isCertificationParamCommonResult.getData().getIs_certification())
            return CommonResult.fail("A0140", "用户未认证");
        Integer userId = isCertificationParamCommonResult.getData().getUserId();

        for (Integer id : param.getMessageIds()) {
            Message message = messageMapper.selectByPrimaryId(id);
            if (message == null)
                return CommonResult.fail("消息不存在");
            if (message.getType() == null
                    || !Objects.equals(message.getUserId(), userId)
                    || !(1 <= message.getType() && message.getType() <= 2))
                return CommonResult.fail("消息与当前登录用户不匹配");
            messageMapper.updateIsRead(id);
        }
        return CommonResult.success();
    }

    /**
     * @apiNote 学生提交状反馈 C-已认证用户
     */
    @Override
    public CommonResult<String> updateState(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, SubmitStateParam param) {
        // 验证登录状态，获取 userID
        CommonResult<IsCertificationParam> isCertificationParamCommonResult = updateUserInfoController.updateUserInfo(httpServletRequest, httpServletResponse);
        if (!Objects.equals(isCertificationParamCommonResult.getCode(), ResultEnum.SUCCESS.getCode())) {
            return CommonResult.fail(isCertificationParamCommonResult.getCode(), isCertificationParamCommonResult.getMessage());
        }
        if (!isCertificationParamCommonResult.getData().getIs_certification())
            return CommonResult.fail("A0140", "用户未认证");
        Integer userId = isCertificationParamCommonResult.getData().getUserId();

        Message message = messageMapper.selectByPrimaryId(param.getMessageId());
        if (message == null)
            return CommonResult.fail("消息不存在");
        if (message.getType() == null
                || !Objects.equals(message.getUserId(), userId)
                || !(1 <= message.getType() && message.getType() <= 2))
            return CommonResult.fail("消息与当前登录用户不匹配");
        if (message.getState() != 0)
            return CommonResult.fail("消息已反馈");
        InterviewStatus interviewStatus = interviewStatusMapper.selectById(message.getInterviewStatusId());
        if (interviewStatus == null
                || interviewStatus.getState() != 3)
            return CommonResult.serverError();
//        if (interviewStatus == null
//                || interviewStatus.getState() != 0)
//            return CommonResult.serverError();
        int result = messageMapper.updateState(param.getMessageId(), param.getState());
        if (result == 1) {
            switch (param.getState()) {
                // 同意
                case 1:
                    interviewStatus.setState(4);
                    interviewStatusMapper.updateById(interviewStatus);
                    break;
                // 拒绝
                case 2:
                    interviewStatus.setState(0);
                    interviewStatusMapper.updateById(interviewStatus);
                    break;
                // 待定（时间冲突）
                case 3:
                    interviewStatus.setState(1);
                    interviewStatusMapper.updateById(interviewStatus);
                    InterviewStatus newis = new InterviewStatus();
                    newis.setUserId(interviewStatus.getUserId());
                    newis.setRound(interviewStatus.getRound());
                    newis.setAdmissionId(interviewStatus.getAdmissionId());
                    newis.setDepartmentId(interviewStatus.getDepartmentId());
                    newis.setState(2);
                    newis.setOrganizationOrder(interviewStatus.getOrganizationOrder());
                    newis.setDepartmentOrder(interviewStatus.getDepartmentOrder());
                    interviewStatusMapper.insert(newis);
                    break;
            }
            return CommonResult.success();
        } else {
            return CommonResult.fail("提交状态失败");
        }
    }
}

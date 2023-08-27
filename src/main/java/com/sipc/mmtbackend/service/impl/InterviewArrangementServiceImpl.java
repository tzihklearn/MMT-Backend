package com.sipc.mmtbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sipc.mmtbackend.mapper.*;
import com.sipc.mmtbackend.mapper.customization.MyInterviewStatusMapper;
import com.sipc.mmtbackend.mapper.customization.MyMajorClassMapper;
import com.sipc.mmtbackend.pojo.domain.*;
import com.sipc.mmtbackend.pojo.domain.po.GroupIntCountPo;
import com.sipc.mmtbackend.pojo.domain.po.InterviewMessagePo;
import com.sipc.mmtbackend.pojo.domain.po.MajorClassPo;
import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.pojo.dto.param.interviewArrangement.MessageSendParam;
import com.sipc.mmtbackend.pojo.dto.param.interviewArrangement.ScheduleParam;
import com.sipc.mmtbackend.pojo.dto.param.interviewArrangement.SiftParam;
import com.sipc.mmtbackend.pojo.dto.param.interviewArrangement.po.MessageSendPo;
import com.sipc.mmtbackend.pojo.dto.result.dataDashboard.po.SiftInfoPo;
import com.sipc.mmtbackend.pojo.dto.result.interviewArrangement.AddressAllResult;
import com.sipc.mmtbackend.pojo.dto.result.interviewArrangement.IAAllResult;
import com.sipc.mmtbackend.pojo.dto.result.interviewArrangement.MessageCheckResult;
import com.sipc.mmtbackend.pojo.dto.result.interviewArrangement.SiftBarResult;
import com.sipc.mmtbackend.pojo.dto.result.interviewArrangement.po.AddressPo;
import com.sipc.mmtbackend.pojo.dto.result.interviewArrangement.po.IAInfoPo;
import com.sipc.mmtbackend.pojo.dto.result.interviewArrangement.po.SiftBarPo;
import com.sipc.mmtbackend.service.InterviewArrangementService;
import com.sipc.mmtbackend.utils.CheckroleBUtil.pojo.BTokenSwapPo;
import com.sipc.mmtbackend.utils.RedisUtil;
import com.sipc.mmtbackend.utils.ThreadLocalContextUtil;
import com.sipc.mmtbackend.utils.TimeTransUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author tzih
 * @version v1.0
 * @since 2023.08.23
 */
@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class InterviewArrangementServiceImpl implements InterviewArrangementService {

    private final AdmissionMapper admissionMapper;

    private final InterviewStatusMapper interviewStatusMapper;

    private final MyInterviewStatusMapper myInterviewStatusMapper;

    private final DepartmentMapper departmentMapper;

    private final AdmissionAddressMapper admissionAddressMapper;

    private final AcaMajorMapper acaMajorMapper;

    private final MajorClassMapper majorClassMapper;

    private final MyMajorClassMapper myMajorClassMapper;

    private AdmissionDepartmentMergeMapper admissionDepartmentMergeMapper;

    private final AdmissionScheduleMapper admissionScheduleMapper;

    private final MessageTemplateMapper messageTemplateMapper;

    private final MessageMapper messageMapper;

    private final RedisUtil redisUtil;

    private final Map<Integer, String> departmentMap = new HashMap<>();

    private final Map<Integer, String> addressMap = new HashMap<>();

    private final Map<Integer, String> classMap = new HashMap<>();

    @Override
    public CommonResult<String> manualSchedule(ScheduleParam scheduleParam) {
        return null;
    }

    @Override
    public CommonResult<String> automaticSchedule(ScheduleParam scheduleParam) {
        return null;
    }

    @Override
    public CommonResult<AddressAllResult> addressAll(Integer round) {

        /*
          鉴权并且获取用户所属社团组织id
         */
        BTokenSwapPo context = ThreadLocalContextUtil.getContext();
        Integer organizationId = context.getOrganizationId();

        Admission admission = admissionMapper.selectOne(
                new QueryWrapper<Admission>()
                        .select("id")
                        .eq("organization_id", organizationId)
                        .orderByDesc("id")
                        .last("limit 1")
        );
        if (admission == null) {
            return CommonResult.fail("社团没有开始纳新");
        }


        List<AddressPo> addressPoList = new ArrayList<>();

        for (AdmissionDepartmentMerge admissionDepartmentMerge : admissionDepartmentMergeMapper.selectList(new QueryWrapper<AdmissionDepartmentMerge>().eq("admission_id", admission.getId()))) {
            for (AdmissionSchedule admissionSchedule : admissionScheduleMapper.selectList(new QueryWrapper<AdmissionSchedule>().eq("admission_department_id", admissionDepartmentMerge.getId()))) {
                for (AdmissionAddress admissionAddress : admissionAddressMapper.selectList(new QueryWrapper<AdmissionAddress>().eq("admission_schedule_id", admissionSchedule.getId()))) {
                    AddressPo addressPo = new AddressPo();
                    addressPo.setId(admissionAddress.getId());
                    addressPo.setAddressName(admissionAddress.getName());
                    addressPoList.add(addressPo);
                }

            }

        }

        AddressAllResult result = new AddressAllResult();

        result.setAddressPoList(addressPoList);

        return CommonResult.success(result);
    }

    @Override
    public CommonResult<SiftBarResult> siftBar(Integer round) {

        /*
          鉴权并且获取用户所属社团组织id
         */
        BTokenSwapPo context = ThreadLocalContextUtil.getContext();
        Integer organizationId = context.getOrganizationId();

        Admission admission = admissionMapper.selectOne(
                new QueryWrapper<Admission>()
                        .select("id")
                        .eq("organization_id", organizationId)
                        .orderByDesc("id")
                        .last("limit 1")
        );
        if (admission == null) {
            return CommonResult.fail("社团没有开始纳新");
        }

        Integer admissionId = admission.getId();

        SiftBarResult result = redisUtil.getString("arrange,siftBar,admissionId:" + admissionId + ",round:" + round, SiftBarResult.class);
        if (result == null) {
            result = new SiftBarResult();

            List<SiftBarPo> departmentList = new ArrayList<>();
            for (GroupIntCountPo departmentGroupCountPo : myInterviewStatusMapper.selectDepartmentCountByAdmissionIdAndRound(admissionId, round)) {

                SiftBarPo siftBarPo = new SiftBarPo();
                siftBarPo.setId(departmentGroupCountPo.getId());
                siftBarPo.setName(departmentMap.get(departmentGroupCountPo.getId()));
                siftBarPo.setNum(departmentGroupCountPo.getCount());
                departmentList.add(siftBarPo);
            }

            List<SiftBarPo> addressIdList = new ArrayList<>();
            for (GroupIntCountPo PlaceGroupCountPo : myInterviewStatusMapper.selectAddressCountByAdmissionIdAndRound(admissionId, round)) {

                SiftBarPo siftBarPo = new SiftBarPo();
                siftBarPo.setId(PlaceGroupCountPo.getId());
                if (PlaceGroupCountPo.getId() == null) {
                    siftBarPo.setId(0);
                    siftBarPo.setName("--");
                } else {
                    siftBarPo.setId(PlaceGroupCountPo.getId());
                    siftBarPo.setName(addressMap.get(PlaceGroupCountPo.getId()));
                }
                siftBarPo.setNum(PlaceGroupCountPo.getCount());
                addressIdList.add(siftBarPo);
            }

            List<SiftBarPo> messageStatusList = new ArrayList<>();
            SiftBarPo siftBarPo = new SiftBarPo();
            messageStatusList.add(siftBarPo);

            int t = 0;

            for (GroupIntCountPo groupIntCountPo : myInterviewStatusMapper.selectMessageStateByAdmissionIdAndRound(admissionId, round)) {
                siftBarPo = new SiftBarPo();

                if (groupIntCountPo.getId() == 2) {
                    siftBarPo.setId(3);
                    siftBarPo.setName("未安排");
                } else {
                    siftBarPo.setId(2);
                    siftBarPo.setName("已安排未通知");
                }
                siftBarPo.setNum(groupIntCountPo.getCount());
                messageStatusList.add(siftBarPo);

                t += groupIntCountPo.getCount();
            }

            siftBarPo = new SiftBarPo();
            siftBarPo.setId(1);
            Long count = interviewStatusMapper.selectCount(
                    new QueryWrapper<InterviewStatus>().eq("admission_id", admissionId).eq("round", round)
            );
            siftBarPo.setName("已安排");
            siftBarPo.setNum(count.intValue() - t);
            messageStatusList.set(0, siftBarPo);

            result.setDepartmentList(departmentList);
            result.setAddressIdList(addressIdList);
            result.setMessageStatusList(messageStatusList);

            redisUtil.setString("arrange,siftBar,admissionId:" + admissionId + ",round:" + round, result, 5, TimeUnit.MINUTES);
        }

        return CommonResult.success(result);
    }

    @Override
    public CommonResult<IAAllResult> all(Integer page, Integer pageNum, Integer round) {

        /*
          鉴权并且获取用户所属社团组织id
         */
        BTokenSwapPo context = ThreadLocalContextUtil.getContext();
        Integer organizationId = context.getOrganizationId();

        Admission admission = admissionMapper.selectOne(
                new QueryWrapper<Admission>()
                        .select("id")
                        .eq("organization_id", organizationId)
                        .orderByDesc("id")
                        .last("limit 1")
        );
        if (admission == null) {
            return CommonResult.fail("社团没有开始纳新");
        }

        if (departmentMap.isEmpty()) {
            for (Department department : departmentMapper.selectList(new QueryWrapper<>())) {
                departmentMap.putIfAbsent(department.getId(), department.getName());
            }
        }

        if (addressMap.isEmpty()) {
            for (AdmissionAddress admissionAddress : admissionAddressMapper.selectList(new QueryWrapper<>())) {
                addressMap.putIfAbsent(admissionAddress.getId(), admissionAddress.getName());
            }
        }

        if (classMap.isEmpty()) {
            for (MajorClassPo majorClassPo : myMajorClassMapper.selectAllAndAcaMajor()) {
                classMap.put(majorClassPo.getId(), majorClassPo.getName());
            }

        }

        int start = (page - 1) * pageNum;
        int end = page * pageNum;

        IAAllResult result = new IAAllResult();

        List<IAInfoPo> iaInfoPos = new ArrayList<>();

        if (round == 1) {
            for (InterviewMessagePo interviewMessagePo : myInterviewStatusMapper.selectIMByAdmissionIdAndRoundFirst(admission.getId(), start, end)) {
                IAInfoPo iaInfoPo = new IAInfoPo();
                iaInfoPo.setId(interviewMessagePo.getId());
                iaInfoPo.setStudentId(interviewMessagePo.getStudentId());
                iaInfoPo.setName(interviewMessagePo.getName());
                iaInfoPo.setClassName(classMap.get(interviewMessagePo.getMajorClassId()));
                iaInfoPo.setNowDepartment(departmentMap.get(interviewMessagePo.getDepartmentId()));

                if (interviewMessagePo.getState() == 9) {
                    iaInfoPo.setInterviewStatus("通过");
                } else if (interviewMessagePo.getState() == 8 || interviewMessagePo.getState() == 0) {
                    iaInfoPo.setInterviewStatus("未通过");
                } else {
                    iaInfoPo.setInterviewStatus("已报名");
                }

                iaInfoPo.setNextTime(TimeTransUtil.transStringToTimeDataDashboard(interviewMessagePo.getNextTime()));
                iaInfoPo.setNextPlace(addressMap.get(interviewMessagePo.getNextPlaceId()));

                if (interviewMessagePo.getMessageStatus() != null) {
                    iaInfoPo.setMessageStatus(1);
                } else if (interviewMessagePo.getNextPlaceId() != null) {
                    iaInfoPo.setMessageStatus(2);
                } else {
                    iaInfoPo.setMessageStatus(3);
                }

                iaInfoPos.add(iaInfoPo);
            }
        } else {
            //TODO: round不为1的处理
            for (InterviewMessagePo interviewMessagePo : myInterviewStatusMapper.selectIMByAdmissionIdAndRound(admission.getId(), round, start, end)) {
                IAInfoPo iaInfoPo = new IAInfoPo();
                iaInfoPo.setId(interviewMessagePo.getId());
                iaInfoPo.setStudentId(interviewMessagePo.getStudentId());
                iaInfoPo.setName(interviewMessagePo.getName());
                iaInfoPo.setClassName(classMap.get(interviewMessagePo.getMajorClassId()));
                iaInfoPo.setNowDepartment(departmentMap.get(interviewMessagePo.getDepartmentId()));

                if (interviewMessagePo.getState() == 9) {
                    iaInfoPo.setInterviewStatus("通过");
                } else if (interviewMessagePo.getState() == 8 || interviewMessagePo.getState() == 0) {
                    iaInfoPo.setInterviewStatus("未通过");
                } else {
                    iaInfoPo.setInterviewStatus("已报名");
                }

                iaInfoPo.setNextTime(TimeTransUtil.transStringToTimeDataDashboard(interviewMessagePo.getNextTime()));
                iaInfoPo.setNextPlace(addressMap.get(interviewMessagePo.getNextPlaceId()));

                if (interviewMessagePo.getMessageStatus() != null) {
                    iaInfoPo.setMessageStatus(1);
                } else if (interviewMessagePo.getNextPlaceId() != null) {
                    iaInfoPo.setMessageStatus(2);
                } else {
                    iaInfoPo.setMessageStatus(3);
                }

                iaInfoPos.add(iaInfoPo);
            }
        }

        Long l = interviewStatusMapper.selectCount(new QueryWrapper<InterviewStatus>().eq("admission_id", admission.getId()).eq("round", round));

        result.setIaInfoPos(iaInfoPos);
        result.setPage(page);

        if (l % pageNum != 0) {
            result.setPageNum((int) (l / pageNum + 1));
        } else {
            result.setPageNum((int) (l / pageNum));
        }

        result.setAllNum(l.intValue());

        return CommonResult.success(result);
    }

    @Override
    public CommonResult<IAAllResult> sift(Integer page, Integer pageNum, Integer round, SiftParam siftParam) {

         /*
          鉴权并且获取用户所属社团组织id
         */
        BTokenSwapPo context = ThreadLocalContextUtil.getContext();
        Integer organizationId = context.getOrganizationId();

        Admission admission = admissionMapper.selectOne(
                new QueryWrapper<Admission>()
                        .select("id")
                        .eq("organization_id", organizationId)
                        .orderByDesc("id")
                        .last("limit 1")
        );
        if (admission == null) {
            return CommonResult.fail("社团没有开始纳新");
        }

        if (siftParam == null) {
            return all(page, pageNum, round);
        }

        int start = (page - 1) * pageNum;
        int end = page * pageNum;

        IAAllResult result = new IAAllResult();

        List<IAInfoPo> iaInfoPos = new ArrayList<>();

        int i = 0;

        int count;
        if (round == 1) {
            List<InterviewMessagePo> interviewMessagePos = myInterviewStatusMapper.selectArrangeByAdmissionIdAndSiftAndRoundFirst(siftParam, admission.getId());
            count = interviewMessagePos.size();
            for (InterviewMessagePo interviewMessagePo : interviewMessagePos) {
                if (i >= start && i < end) {
                    IAInfoPo iaInfoPo = new IAInfoPo();
                    iaInfoPo.setId(interviewMessagePo.getId());
                    iaInfoPo.setStudentId(interviewMessagePo.getStudentId());
                    iaInfoPo.setName(interviewMessagePo.getName());
                    iaInfoPo.setClassName(classMap.get(interviewMessagePo.getMajorClassId()));
                    iaInfoPo.setNowDepartment(departmentMap.get(interviewMessagePo.getDepartmentId()));

                    if (interviewMessagePo.getState() == 9) {
                        iaInfoPo.setInterviewStatus("通过");
                    } else if (interviewMessagePo.getState() == 8 || interviewMessagePo.getState() == 0) {
                        iaInfoPo.setInterviewStatus("未通过");
                    } else {
                        iaInfoPo.setInterviewStatus("已报名");
                    }

                    iaInfoPo.setNextTime(TimeTransUtil.transStringToTimeDataDashboard(interviewMessagePo.getNextTime()));
                    iaInfoPo.setNextPlace(addressMap.get(interviewMessagePo.getNextPlaceId()));

                    if (interviewMessagePo.getMessageStatus() != null) {
                        iaInfoPo.setMessageStatus(1);
                    } else if (interviewMessagePo.getNextPlaceId() != null) {
                        iaInfoPo.setMessageStatus(2);
                    } else {
                        iaInfoPo.setMessageStatus(3);
                    }

                    iaInfoPos.add(iaInfoPo);
                }
                ++i;
                if (i >= end) {
                    break;
                }

            }
        } else {
            //TODO: round不为1的处理
            List<InterviewMessagePo> interviewMessagePos = myInterviewStatusMapper.selectArrangeByAdmissionIdAndSiftAndRound(siftParam, admission.getId(), round);
            count = interviewMessagePos.size();
            for (InterviewMessagePo interviewMessagePo : interviewMessagePos) {
                if (i >= start && i < end) {
                    IAInfoPo iaInfoPo = new IAInfoPo();
                    iaInfoPo.setId(interviewMessagePo.getId());
                    iaInfoPo.setStudentId(interviewMessagePo.getStudentId());
                    iaInfoPo.setName(interviewMessagePo.getName());
                    iaInfoPo.setClassName(classMap.get(interviewMessagePo.getMajorClassId()));
                    iaInfoPo.setNowDepartment(departmentMap.get(interviewMessagePo.getDepartmentId()));

                    if (interviewMessagePo.getState() == 9) {
                        iaInfoPo.setInterviewStatus("通过");
                    } else if (interviewMessagePo.getState() == 8 || interviewMessagePo.getState() == 0) {
                        iaInfoPo.setInterviewStatus("未通过");
                    } else {
                        iaInfoPo.setInterviewStatus("已报名");
                    }

                    iaInfoPo.setNextTime(TimeTransUtil.transStringToTimeDataDashboard(interviewMessagePo.getNextTime()));
                    iaInfoPo.setNextPlace(addressMap.get(interviewMessagePo.getNextPlaceId()));

                    if (interviewMessagePo.getMessageStatus() != null) {
                        iaInfoPo.setMessageStatus(1);
                    } else if (interviewMessagePo.getNextPlaceId() != null) {
                        iaInfoPo.setMessageStatus(2);
                    } else {
                        iaInfoPo.setMessageStatus(3);
                    }

                    iaInfoPos.add(iaInfoPo);
                }
                ++i;
                if (i >= end) {
                    break;
                }
            }
        }

        result.setIaInfoPos(iaInfoPos);
        result.setPage(page);

        if (count % pageNum != 0) {
            result.setPageNum(count / pageNum + 1);
        } else {
            result.setPageNum(count / pageNum);
        }

        result.setAllNum(count);

        return CommonResult.success(result);
    }

    @Override
    public CommonResult<MessageCheckResult> messageCheck(Integer round) {

        /*
          鉴权并且获取用户所属社团组织id
         */
        BTokenSwapPo context = ThreadLocalContextUtil.getContext();
        Integer organizationId = context.getOrganizationId();

        Admission admission = admissionMapper.selectOne(
                new QueryWrapper<Admission>()
                        .select("id")
                        .eq("organization_id", organizationId)
                        .orderByDesc("id")
                        .last("limit 1")
        );
        if (admission == null) {
            return CommonResult.fail("社团没有开始纳新");
        }

        MessageTemplate messageTemplate = messageTemplateMapper.selectOne(
                new QueryWrapper<MessageTemplate>()
                        .eq("organization_id", organizationId)
                        .eq("type", 1)
                        .orderByDesc("id")
                        .last("limit 1")
        );

        Long un = interviewStatusMapper.selectCount(
                new QueryWrapper<InterviewStatus>()
                        .eq("admission_id", admission.getId())
                        .eq("round", round)
                        .eq("state", 3)
        );

        Long no = interviewStatusMapper.selectCount(
                new QueryWrapper<InterviewStatus>()
                        .eq("admission_id", admission.getId())
                        .eq("round", round)
                        .eq("state", 2)
        );

        Long all = interviewStatusMapper.selectCount(
                new QueryWrapper<InterviewStatus>()
                        .eq("admission_id", admission.getId())
                        .eq("round", round)
        );

        MessageCheckResult result = new MessageCheckResult();
        if (messageTemplate != null) {
            result.setMessageTemple(messageTemplate.getMessageTemplate());
        }
        result.setAllNum((int) (all - no));
        result.setNotifiedNum((int) (all - no- un));
        result.setNotifiedNum(Math.toIntExact(un));

        return CommonResult.success(result);
    }

    @Override
    public CommonResult<String> messageSend(MessageSendParam messageSendParam) {

        /*
          鉴权并且获取用户所属社团组织id
         */
        BTokenSwapPo context = ThreadLocalContextUtil.getContext();
        Integer organizationId = context.getOrganizationId();

        Admission admission = admissionMapper.selectOne(
                new QueryWrapper<Admission>()
                        .select("id")
                        .eq("organization_id", organizationId)
                        .orderByDesc("id")
                        .last("limit 1")
        );
        if (admission == null) {
            return CommonResult.fail("社团没有开始纳新");
        }

        if (messageSendParam.getMessageSendPoList() != null) {
            for (MessageSendPo messageSendPo : messageSendParam.getMessageSendPoList()) {

                InterviewStatus interviewStatus = new InterviewStatus();
                interviewStatus.setId(messageSendPo.getInterviewId());
                interviewStatus.setState(4);
                int updateNum = interviewStatusMapper.updateById(interviewStatus);
                if (updateNum != 1) {
                    return CommonResult.fail("安排出错");
                }
                Message message = new Message();
                message.setMessage(messageSendParam.getMessage());
                message.setTime(LocalDateTime.now());
                message.setIsRead((byte) 0);
                message.setOrganizationId(organizationId);
                message.setUserId(messageSendPo.getUserId());
                message.setType(2);
                message.setInterviewStatusId(messageSendPo.getInterviewId());
                message.setIsDeleted((byte) 0);
                messageMapper.insert(message);
            }
        }

        return CommonResult.success();
    }
}
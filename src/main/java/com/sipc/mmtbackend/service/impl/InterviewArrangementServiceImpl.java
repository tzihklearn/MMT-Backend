package com.sipc.mmtbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.sipc.mmtbackend.mapper.*;
import com.sipc.mmtbackend.mapper.customization.MyAdmissionAddressMapper;
import com.sipc.mmtbackend.mapper.customization.MyInterviewStatusMapper;
import com.sipc.mmtbackend.mapper.customization.MyMajorClassMapper;
import com.sipc.mmtbackend.pojo.domain.*;
import com.sipc.mmtbackend.pojo.domain.po.GroupIntCountPo;
import com.sipc.mmtbackend.pojo.domain.po.InterviewMessagePo;
import com.sipc.mmtbackend.pojo.domain.po.MajorClassPo;
import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.pojo.dto.param.interviewArrangement.*;
import com.sipc.mmtbackend.pojo.dto.param.interviewArrangement.po.MessageSendPo;
import com.sipc.mmtbackend.pojo.dto.result.interviewArrangement.AddressAllResult;
import com.sipc.mmtbackend.pojo.dto.result.interviewArrangement.IAAllResult;
import com.sipc.mmtbackend.pojo.dto.result.interviewArrangement.MessageCheckResult;
import com.sipc.mmtbackend.pojo.dto.result.interviewArrangement.SiftBarResult;
import com.sipc.mmtbackend.pojo.dto.result.interviewArrangement.po.AddressPo;
import com.sipc.mmtbackend.pojo.dto.result.interviewArrangement.po.IAInfoPo;
import com.sipc.mmtbackend.pojo.dto.result.interviewArrangement.po.SiftBarPo;
import com.sipc.mmtbackend.pojo.exceptions.DateBaseException;
import com.sipc.mmtbackend.service.InterviewArrangementService;
import com.sipc.mmtbackend.utils.CheckroleBUtil.pojo.BTokenSwapPo;
import com.sipc.mmtbackend.utils.RedisUtil;
import com.sipc.mmtbackend.utils.ThreadLocalContextUtil;
import com.sipc.mmtbackend.utils.TimeTransUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
@Slf4j
public class InterviewArrangementServiceImpl implements InterviewArrangementService {

    private final AdmissionMapper admissionMapper;

    private final InterviewStatusMapper interviewStatusMapper;

    private final MyInterviewStatusMapper myInterviewStatusMapper;

    private final DepartmentMapper departmentMapper;

    private final AdmissionAddressMapper admissionAddressMapper;

    private final AcaMajorMapper acaMajorMapper;

    private final MajorClassMapper majorClassMapper;

    private final MyMajorClassMapper myMajorClassMapper;

    private final AdmissionDepartmentMergeMapper admissionDepartmentMergeMapper;

    private final AdmissionScheduleMapper admissionScheduleMapper;

    private final MessageTemplateMapper messageTemplateMapper;

    private final MessageMapper messageMapper;

    private final MyAdmissionAddressMapper myAdmissionAddressMapper;

    private final RedisUtil redisUtil;

    private final Map<Integer, String> departmentMap = new HashMap<>();

    private final Map<Integer, String> addressMap = new HashMap<>();

    private final Map<Integer, String> classMap = new HashMap<>();

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<String> manualSchedule(ScheduleParam scheduleParam) throws DateBaseException {

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

        int admissionId = admission.getId();

        Map<Integer, List<Integer>> interviewIdMap = new HashMap<>();
        Map<Integer, List<Integer>> addressIdMap = new HashMap<>();

        //TODO：要使用多线程异步优化
        for (Integer interviewId : scheduleParam.getInterviewIdList()) {
            InterviewStatus interviewStatus = interviewStatusMapper.selectById(interviewId);
            if (interviewStatus == null || interviewStatus.getAdmissionId() != admissionId) {
                return CommonResult.fail("该社团不存在的数据");
            }
            List<Integer> integerList = interviewIdMap.get(interviewStatus.getDepartmentId());
            if (integerList == null) {
                integerList = new ArrayList<>();
                integerList.add(interviewId);
                interviewIdMap.put(interviewStatus.getDepartmentId(), integerList);
            } else {
                integerList.add(interviewId);
            }
        }

        for (Integer addressId : scheduleParam.getAddressIdList()) {
            AdmissionDepartmentMerge admissionDepartmentMerge = myAdmissionAddressMapper.selectDepartmentAndId(addressId);

            if (admissionDepartmentMerge == null || admissionDepartmentMerge.getAdmissionId() != admissionId) {
                return CommonResult.fail("该社团不存在的数据");
            }

            List<Integer> integerList = addressIdMap.get(admissionDepartmentMerge.getDepartmentId());
            if (integerList == null) {
                integerList = new ArrayList<>();
                integerList.add(addressId);
                addressIdMap.put(admissionDepartmentMerge.getDepartmentId(), integerList);
            } else {
                integerList.add(addressId);
            }
        }

        for (Map.Entry<Integer, List<Integer>> addressIdMapEntry : addressIdMap.entrySet()) {
            Integer departmentId = addressIdMapEntry.getKey();
            List<Integer> addressIdList = addressIdMapEntry.getValue();
            List<Integer> interviewIdList= interviewIdMap.get(departmentId);
            if (interviewIdList != null) {
                //TODO：要使用多线程异步优化
                int i = 0;
                int t = interviewIdList.size() / addressIdList.size();

                int size = addressIdList.size();

                if (t == 0) {
                    for (Integer interviewId : interviewIdList) {
                        InterviewStatus interviewStatus = new InterviewStatus();
                        interviewStatus.setId(interviewId);
                        interviewStatus.setAdmissionAddressId(addressIdList.get(i));
                        interviewStatus.setStartTime(TimeTransUtil.transLongToTime(scheduleParam.getStartTime()));
                        interviewStatus.setEndTime(TimeTransUtil.transLongToTime(scheduleParam.getStartTime() + scheduleParam.getTime() * 60 * 1000));
                        interviewStatus.setState(3);
                        int updateNum = interviewStatusMapper.updateById(interviewStatus);
                        if (updateNum != 1) {
                            log.error("手动面试安排接口异常，interview_status表更新数异常，受影响的行数：{}，更新的数据：{}", updateNum, interviewStatus);
                            throw new DateBaseException("数据库更新数据异常");
                        }
                        ++i;
                    }
                } else {
                    for (Integer interviewId : interviewIdList) {
                        InterviewStatus interviewStatus = new InterviewStatus();
                        interviewStatus.setId(interviewId);
//                        interviewStatus.setAdmissionAddressId(addressIdList.get(i%t));
                        interviewStatus.setAdmissionAddressId(addressIdList.get(i%size));
                        interviewStatus.setStartTime(TimeTransUtil.transLongToTime(scheduleParam.getStartTime() + (long) scheduleParam.getTime() * 60 * (i/size) * 1000));
                        interviewStatus.setEndTime(TimeTransUtil.transLongToTime(scheduleParam.getStartTime() + (long) scheduleParam.getTime() * 60 * ((i + size)/size) * 1000));
                        interviewStatus.setState(3);
                        int updateNum = interviewStatusMapper.updateById(interviewStatus);
                        if (updateNum != 1) {
                            log.error("手动面试安排接口异常，interview_status表更新数异常，受影响的行数：{}，更新的数据：{}", updateNum, interviewStatus);
                            throw new DateBaseException("数据库更新数据异常");
                        }
                        ++i;
                    }
                }
            }
        }

        return CommonResult.success("操作成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<String> automaticSchedule(ScheduleParam scheduleParam) throws DateBaseException {
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

        int admissionId = admission.getId();

        Map<Integer, List<Integer>> interviewIdMap = new HashMap<>();
        Map<Integer, List<Integer>> addressIdMap = new HashMap<>();

        int flag = 0;

        //TODO：要使用多线程异步优化
        for (Integer interviewId : scheduleParam.getInterviewIdList()) {
            InterviewStatus interviewStatus = interviewStatusMapper.selectById(interviewId);
            if (interviewStatus == null || interviewStatus.getAdmissionId() != admissionId) {
                return CommonResult.fail("该社团不存在的数据");
            }
            List<Integer> integerList = interviewIdMap.get(interviewStatus.getDepartmentId());
            if (integerList == null) {
                integerList = new ArrayList<>();
                integerList.add(interviewId);
                interviewIdMap.put(interviewStatus.getDepartmentId(), integerList);
            } else {
                integerList.add(interviewId);
            }
        }

        for (Integer addressId : scheduleParam.getAddressIdList()) {
            AdmissionDepartmentMerge admissionDepartmentMerge = myAdmissionAddressMapper.selectDepartmentAndId(addressId);

            if (admissionDepartmentMerge == null || admissionDepartmentMerge.getAdmissionId() != admissionId) {
                return CommonResult.fail("该社团不存在的数据");
            }

            List<Integer> integerList = addressIdMap.get(admissionDepartmentMerge.getDepartmentId());
            if (integerList == null) {
                integerList = new ArrayList<>();
                integerList.add(addressId);
                addressIdMap.put(admissionDepartmentMerge.getDepartmentId(), integerList);
            } else {
                integerList.add(addressId);
            }
        }

        for (Map.Entry<Integer, List<Integer>> addressIdMapEntry : addressIdMap.entrySet()) {
            Integer departmentId = addressIdMapEntry.getKey();
            List<Integer> addressIdList = addressIdMapEntry.getValue();
            List<Integer> interviewIdList= interviewIdMap.get(departmentId);
            if (interviewIdList != null) {
                //TODO：要使用多线程异步优化
                int i = 0;
                int t = interviewIdList.size() / addressIdList.size();

                int size = addressIdList.size();

                if (t == 0) {
                    for (Integer interviewId : interviewIdList) {
                        InterviewStatus interviewStatus = new InterviewStatus();
                        interviewStatus.setId(interviewId);
                        interviewStatus.setAdmissionAddressId(addressIdList.get(i));
                        interviewStatus.setStartTime(TimeTransUtil.transLongToTime(scheduleParam.getStartTime()));
                        long endTime = scheduleParam.getStartTime() + scheduleParam.getTime() * 60 * 1000;
                        interviewStatus.setEndTime(TimeTransUtil.transLongToTime(endTime));
                        interviewStatus.setState(3);
                        if (endTime > scheduleParam.getEndTime()) {
                            break;
                        }
                        int updateNum = interviewStatusMapper.updateById(interviewStatus);
                        if (updateNum != 1) {
                            log.error("手动面试安排接口异常，interview_status表更新数异常，受影响的行数：{}，更新的数据：{}", updateNum, interviewStatus);
                            throw new DateBaseException("数据库更新数据异常");
                        }
                        ++i;
                    }
                } else {
                    for (Integer interviewId : interviewIdList) {
                        InterviewStatus interviewStatus = new InterviewStatus();
                        interviewStatus.setId(interviewId);
//                        interviewStatus.setAdmissionAddressId(addressIdList.get(i%t));
                        interviewStatus.setAdmissionAddressId(addressIdList.get(i%size));
                        interviewStatus.setStartTime(TimeTransUtil.transLongToTime(scheduleParam.getStartTime() + (long) scheduleParam.getTime() * 60 * (i/size) * 1000));
                        long endTime = scheduleParam.getStartTime() + (long) scheduleParam.getTime() * 60 * ((i + size)/size) * 1000;
                        interviewStatus.setEndTime(TimeTransUtil.transLongToTime(endTime));
                        interviewStatus.setState(3);
                        if (endTime > scheduleParam.getEndTime()) {
                            break;
                        }
                        int updateNum = interviewStatusMapper.updateById(interviewStatus);
                        if (updateNum != 1) {
                            log.error("手动面试安排接口异常，interview_status表更新数异常，受影响的行数：{}，更新的数据：{}", updateNum, interviewStatus);
                            throw new DateBaseException("数据库更新数据异常");
                        }
                        ++i;
                    }
                }
            }
        }

        return CommonResult.success("操作成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<AddressAllResult> saveAddress(SaveAddressParam saveAddressParam) throws DateBaseException {

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

        AdmissionDepartmentMerge admissionDepartmentMerge = admissionDepartmentMergeMapper.selectOne(
                new QueryWrapper<AdmissionDepartmentMerge>()
//                        .select("id")
                        .eq("admission_id", admission.getId())
                        .eq("department_id", saveAddressParam.getDepartmentId())
        );

        if (admissionDepartmentMerge == null) {
            return CommonResult.fail("该部门未加入纳新");
        }

        AdmissionSchedule admissionSchedule = admissionScheduleMapper.selectOne(
                new QueryWrapper<AdmissionSchedule>()
//                        .select("id")
                        .eq("admission_department_id", admissionDepartmentMerge.getId())
                        .eq("round", saveAddressParam.getRound())
        );

        if (admissionSchedule == null) {
            admissionSchedule = new AdmissionSchedule();
            admissionSchedule.setAdmissionDepartmentId(admissionDepartmentMerge.getId());
            admissionSchedule.setRound(saveAddressParam.getRound());
            admissionSchedule.setIsDeleted((byte) 0);
            int insertNum = admissionScheduleMapper.insert(admissionSchedule);
            if (insertNum != 1) {
                log.error("adsa");
                throw new DateBaseException("数据库插入异常");
            }
        }

        AdmissionAddress admissionAddress = new AdmissionAddress();
        admissionAddress.setName(saveAddressParam.getName());
        admissionAddress.setAdmissionScheduleId(admissionSchedule.getId());
        admissionAddress.setIsDeleted((byte) 0);
        int insertNum = admissionAddressMapper.insert(admissionAddress);
        if (insertNum != 1) {
            log.error("设置社团纳新组织面试地点接口异常，admission_address表新增数据异常，影响行数：{}，新增数据：{}", insertNum, admissionAddress);
            throw new DateBaseException("删除数据库数据异常");
        }

        addressMap.putIfAbsent(admissionAddress.getId(), admissionAddress.getName());

        return addressAll(saveAddressParam.getRound());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<AddressAllResult> deletedAddress(DeletedAddressParam deletedAddressParam) throws DateBaseException {

        /*
          鉴权并且获取用户所属社团组织id
         */
        BTokenSwapPo context = ThreadLocalContextUtil.getContext();
        Integer organizationId = context.getOrganizationId();

        Admission admission = admissionMapper.selectOne(
                new QueryWrapper<Admission>()
                        .select("id")
                        .select("organization_id")
                        .eq("organization_id", organizationId)
                        .orderByDesc("id")
                        .last("limit 1")
        );
        if (admission == null) {
            return CommonResult.fail("社团没有开始纳新");
        }

        int deleteNum = admissionAddressMapper.deleteById(deletedAddressParam.getAddressId());
        if (deleteNum != 1) {
            log.error("删除社团部门面试地点接口异常，admission_address表删除数据异常，影响行数：{}，删除数据id：{}", deleteNum, deletedAddressParam.getAddressId());
            throw new DateBaseException("数据库删除数据异常");
        }

//        interviewStatusMapper.update(new InterviewStatus(),
//                new UpdateWrapper<InterviewStatus>()
//                        .eq("admission_address_id", deletedAddressParam.getAddressId())
//                        .and(i -> i.eq("state", 3).or(j->j.eq("state", 4)))
//                        .set("state", 2)
//                        .set("admission_address_id", null)
//                        .set("start_time", null)
//                        .set("end_time", null)
//                        .set("is_message", 0)
//        );

        List<InterviewStatus> interviewStatusIds = interviewStatusMapper.selectList(
                new QueryWrapper<InterviewStatus>()
                        .select("id")
                        .select("user_id")
                        .eq("admission_address_id", deletedAddressParam.getAddressId())
                        .eq("state", 4)
                        .eq("is_message", 2)
        );

        interviewStatusMapper.updateByRAdmissionAddress(deletedAddressParam.getAddressId(), deletedAddressParam.getRound());

        messageMapper.insertRAddress(interviewStatusIds, admission.getOrganizationId(), LocalDateTime.now());

        addressMap.remove(deletedAddressParam.getAddressId());

        return addressAll(deletedAddressParam.getRound());
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

//                    addressMap.putIfAbsent(admissionAddress.getId(), admissionAddress.getName());
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
                iaInfoPo.setUserId(interviewMessagePo.getUserId());
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
                iaInfoPo.setUserId(interviewMessagePo.getUserId());
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


                    IAInfoPo iaInfoPo = new IAInfoPo();
                    iaInfoPo.setId(interviewMessagePo.getId());
                    iaInfoPo.setUserId(interviewMessagePo.getUserId());
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

                    if ( siftParam.getSearch() != null && !siftParam.getSearch().isEmpty() && !iaInfoPo.toString().contains(siftParam.getSearch())) {
                        --count;
                        continue;
                    }

                if (i >= start && i < end) {
                    iaInfoPos.add(iaInfoPo);

                }
                ++i;
//                if (i >= end) {
//                    break;
//                }

            }
        } else {
            //TODO: round不为1的处理
            List<InterviewMessagePo> interviewMessagePos = myInterviewStatusMapper.selectArrangeByAdmissionIdAndSiftAndRound(siftParam, admission.getId(), round);
            count = interviewMessagePos.size();
            for (InterviewMessagePo interviewMessagePo : interviewMessagePos) {


                    IAInfoPo iaInfoPo = new IAInfoPo();
                    iaInfoPo.setId(interviewMessagePo.getId());
                    iaInfoPo.setUserId(interviewMessagePo.getUserId());
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

                    if (interviewMessagePo.getMessageStatus() != null && interviewMessagePo.getState() != 3) {
                        iaInfoPo.setMessageStatus(1);
                    } else if (interviewMessagePo.getNextPlaceId() != null) {
                        iaInfoPo.setMessageStatus(2);
                    } else {
                        iaInfoPo.setMessageStatus(3);
                    }

                    if ( siftParam.getSearch() != null && !siftParam.getSearch().isEmpty() && !iaInfoPo.toString().contains(siftParam.getSearch())) {
                        --count;
                        continue;
                    }

                if (i >= start && i < end) {
                    iaInfoPos.add(iaInfoPo);

                }
                ++i;
//                if (i >= end) {
//                    break;
//                }
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
        result.setNotifiedNum((int) (all - no - un));
        result.setNotNotifiedNum(Math.toIntExact(un));

        return CommonResult.success(result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<String> messageSend(MessageSendParam messageSendParam) throws DateBaseException {

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
//                interviewStatus.setId(messageSendPo.getInterviewId());
                interviewStatus.setState(4);
//                int updateNum = interviewStatusMapper.updateById(interviewStatus);
                int updateNum = interviewStatusMapper.update(interviewStatus,
                        new UpdateWrapper<InterviewStatus>()
                                .eq("id", messageSendPo.getInterviewId())
                                .eq("state", 3)
                );
                if (updateNum == 0) {
                    break;
                }
                if (updateNum != 1) {
//                    return CommonResult.fail("安排出错");
                    throw new DateBaseException("数据库更新异常");
                }
                Message message = new Message();
                message.setMessage(messageSendPo.getMessage());
                message.setTime(LocalDateTime.now());
                message.setIsRead((byte) 0);
                message.setOrganizationId(organizationId);
                message.setUserId(messageSendPo.getUserId());
                message.setType(2);
                message.setState(0);
                message.setInterviewStatusId(messageSendPo.getInterviewId());
                message.setIsDeleted((byte) 0);
                messageMapper.insert(message);
            }
        }

        return CommonResult.success();
    }
}

package com.sipc.mmtbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sipc.mmtbackend.mapper.*;
import com.sipc.mmtbackend.mapper.customization.MyInterviewStatusMapper;
import com.sipc.mmtbackend.mapper.customization.MyMajorClassMapper;
import com.sipc.mmtbackend.pojo.domain.*;
import com.sipc.mmtbackend.pojo.domain.po.GroupIntCountPo;
import com.sipc.mmtbackend.pojo.domain.po.GroupLocalTimeCountPo;
import com.sipc.mmtbackend.pojo.domain.po.MajorClassPo;
import com.sipc.mmtbackend.pojo.domain.po.MyInterviewStatusPo;
import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.pojo.dto.param.dataDashboard.EvaluationChangeParam;
import com.sipc.mmtbackend.pojo.dto.param.dataDashboard.SiftParam;
import com.sipc.mmtbackend.pojo.dto.result.DataDashboardExportResult;
import com.sipc.mmtbackend.pojo.dto.result.dataDashboard.DataDashboardInfoResult;
import com.sipc.mmtbackend.pojo.dto.result.dataDashboard.EvaluationInfoResult;
import com.sipc.mmtbackend.pojo.dto.result.dataDashboard.ResumeInfoResult;
import com.sipc.mmtbackend.pojo.dto.result.dataDashboard.RoundResult;
import com.sipc.mmtbackend.pojo.dto.result.dataDashboard.po.*;
import com.sipc.mmtbackend.service.DataDashboardService;
import com.sipc.mmtbackend.utils.CheckroleBUtil.pojo.BTokenSwapPo;
import com.sipc.mmtbackend.utils.RedisUtil;
import com.sipc.mmtbackend.utils.ThreadLocalContextUtil;
import com.sipc.mmtbackend.utils.TimeTransUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author tzih
 * @version v1.0
 * @since 2023.05.31
 */
@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Slf4j
public class DataDashboardServiceImpl implements DataDashboardService {

    private final AdmissionMapper admissionMapper;

    private final InterviewStatusMapper interviewStatusMapper;

    private final MyInterviewStatusMapper myInterviewStatusMapper;

    private final OrganizationMapper organizationMapper;

    private final DepartmentMapper departmentMapper;

    private final AdmissionAddressMapper admissionAddressMapper;

    private final AcaMajorMapper acaMajorMapper;

    private final MajorClassMapper majorClassMapper;

    private final MyMajorClassMapper myMajorClassMapper;

    private final AdmissionQuestionMapper admissionQuestionMapper;

    private final UserInfoMapper userInfoMapper;

    private final QuestionDataMapper questionDataMapper;

    private final RegistrationFromDataMapper registrationFromDataMapper;

    private final MessageMapper messageMapper;

    private final UserVolunteerMapper userVolunteerMapper;

    private final InterviewEvaluationMapper interviewEvaluationMapper;

    private final InterviewQuestionMapper interviewQuestionMapper;

    private final UserBMapper userBMapper;

    private final QuestionScoreMapper questionScoreMapper;

//    private final

    private final RedisUtil redisUtil;

    private final Map<Integer, String> departmentMap = new HashMap<>();

    private final Map<Integer, String> addressMap = new HashMap<>();

    private final Map<Integer, String> classMap = new HashMap<>();

    @Override
    public CommonResult<DataDashboardInfoResult> all(Integer page, Integer pageNum) {

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

        //从redis中获取数据面板导航栏
        SiftBarPo siftBarPo = redisUtil.getString("data,siftBar+" + admissionId, SiftBarPo.class);

//        Organization organization = organizationMapper.selectById(organizationId);
//
//        String organizationName = organization.getName();

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

        List<DataDashboardInfoPo> interviewerInfoList = new ArrayList<>();

        if (siftBarPo == null) {

            siftBarPo = new SiftBarPo();

//            Map<Integer, Integer> admissionOrderBarMap = new HashMap<>();
//
//            Map<Integer, Integer> departmentOrderBarMap = new HashMap<>();
//
//            Map<Integer, Integer> nowDepartmentBarMap = new HashMap<>();
//
//            Map<String, Integer> nextTimeBarMap = new HashMap<>();
//
//            Map<Integer, Integer> nextPlaceBarMap = new HashMap<>();


            List<SiftInfoPo> organizationOrderBar = new ArrayList<>();
            for (GroupIntCountPo organizationOrderGroupCountPo : myInterviewStatusMapper.selectOrganizationOrderCountByAdmissionId(admissionId)) {

                SiftInfoPo siftInfoPo = new SiftInfoPo();
                siftInfoPo.setInfo(organizationOrderGroupCountPo.getId());
                if (organizationOrderGroupCountPo.getId() == 1) {
                    siftInfoPo.setSiftName("第一志愿");
                } else if (organizationOrderGroupCountPo.getId() == 2) {
                    siftInfoPo.setSiftName("第二志愿");
                } else {
                    siftInfoPo.setSiftName("第三志愿");
                }
                siftInfoPo.setNumber(organizationOrderGroupCountPo.getCount());
                organizationOrderBar.add(siftInfoPo);
            }

            List<SiftInfoPo> departmentOrderBar = new ArrayList<>();
            for (GroupIntCountPo departmentOrderGroupCountPo : myInterviewStatusMapper.selectDepartmentOrderCountByAdmissionId(admissionId)) {

                SiftInfoPo siftInfoPo = new SiftInfoPo();
                siftInfoPo.setInfo(departmentOrderGroupCountPo.getId());
                if (departmentOrderGroupCountPo.getId() == 1) {
                    siftInfoPo.setSiftName("第一志愿");
                } else if (departmentOrderGroupCountPo.getId() == 2) {
                    siftInfoPo.setSiftName("第二志愿");
                } else {
                    siftInfoPo.setSiftName("第三志愿");
                }
                siftInfoPo.setNumber(departmentOrderGroupCountPo.getCount());
                departmentOrderBar.add(siftInfoPo);
            }

            List<SiftInfoPo> departmentBar = new ArrayList<>();
            for (GroupIntCountPo departmentGroupCountPo : myInterviewStatusMapper.selectDepartmentCountByAdmissionId(admissionId)) {

                SiftInfoPo siftInfoPo = new SiftInfoPo();
                siftInfoPo.setInfo(departmentGroupCountPo.getId());
                siftInfoPo.setSiftName(departmentMap.get(departmentGroupCountPo.getId()));
                siftInfoPo.setNumber(departmentGroupCountPo.getCount());
                departmentBar.add(siftInfoPo);
            }

            List<SiftInfoPo> nextTimeBar = new ArrayList<>();
            for (GroupLocalTimeCountPo nextTimeGroupCountPo : myInterviewStatusMapper.selectNextTimeCountByAdmissionId(admissionId)) {

                SiftInfoPo siftInfoPo = new SiftInfoPo();
//                siftInfoPo.setInfo(nextTimeGroupCountPo.getId());
                if (nextTimeGroupCountPo.getId() == null) {
                    siftInfoPo.setSiftName("--");
                } else {
                    siftInfoPo.setSiftName(TimeTransUtil.transStringToTimeDataDashboard(nextTimeGroupCountPo.getId()));
                }
                siftInfoPo.setNumber(nextTimeGroupCountPo.getCount());
                nextTimeBar.add(siftInfoPo);
            }

            List<SiftInfoPo> nextPlaceBar = new ArrayList<>();
            for (GroupIntCountPo PlaceGroupCountPo : myInterviewStatusMapper.selectAddressCountByAdmissionId(admissionId)) {

                SiftInfoPo siftInfoPo = new SiftInfoPo();
                siftInfoPo.setInfo(PlaceGroupCountPo.getId());
                if (PlaceGroupCountPo.getId() == null) {
                    siftInfoPo.setInfo(0);
                    siftInfoPo.setSiftName("--");
                } else {
                    siftInfoPo.setInfo(PlaceGroupCountPo.getId());
                    siftInfoPo.setSiftName(addressMap.get(PlaceGroupCountPo.getId()));
                }
                siftInfoPo.setNumber(PlaceGroupCountPo.getCount());
                nextPlaceBar.add(siftInfoPo);
            }

            siftBarPo.setOrganizationOrderBar(organizationOrderBar);
            siftBarPo.setDepartmentOrderBar(departmentOrderBar);
            siftBarPo.setNowDepartmentBar(departmentBar);
            siftBarPo.setNextTimeBar(nextTimeBar);
            siftBarPo.setNextPlaceBar(nextPlaceBar);


            redisUtil.setString("data,siftBar+" + admissionId, siftBarPo, 5, TimeUnit.MINUTES);

        }

        int i = 0;

        int start = (page - 1) * pageNum;
        int end = page * pageNum;


        i = start;

        for (MyInterviewStatusPo interviewStatus : myInterviewStatusMapper.selectAllAndUserInfoByAdmissionIdLimit(admissionId, start, end)) {

//                admissionOrderBarMap.merge(interviewStatus.getOrganizationOrder(), 1, Integer::sum);
//                departmentOrderBarMap.merge(interviewStatus.getDepartmentOrder(), 1, Integer::sum);
//                nowDepartmentBarMap.merge(interviewStatus.getDepartmentId(), 1, Integer::sum);
//                nextTimeBarMap.merge(interviewStatus.getStartTime().toString(), 1, Integer::sum);
//                nextPlaceBarMap.merge(interviewStatus.getAdmissionAddressId(), 1, Integer::sum);


                DataDashboardInfoPo dataDashboardInfoPo = new DataDashboardInfoPo();
                dataDashboardInfoPo.setId(interviewStatus.getId());
                dataDashboardInfoPo.setStudentId(interviewStatus.getStudentId());
                dataDashboardInfoPo.setName(interviewStatus.getName());
                dataDashboardInfoPo.setClassName(classMap.get(interviewStatus.getMajorClassId()));
                dataDashboardInfoPo.setPhone(interviewStatus.getPhone());

                if (interviewStatus.getOrganizationOrder() == 1) {
                    dataDashboardInfoPo.setOrganizationOrder("第一志愿");
                } else if (interviewStatus.getOrganizationOrder() == 2) {
                    dataDashboardInfoPo.setOrganizationOrder("第二志愿");
                } else {
                    dataDashboardInfoPo.setOrganizationOrder("第三志愿");
                }

                if (interviewStatus.getDepartmentOrder() == 1) {
                    dataDashboardInfoPo.setDepartmentOrder("第一志愿");
                } else if (interviewStatus.getDepartmentOrder() == 2) {
                    dataDashboardInfoPo.setDepartmentOrder("第二志愿");
                } else {
                    dataDashboardInfoPo.setDepartmentOrder("第三志愿");
                }

                dataDashboardInfoPo.setNowDepartment(departmentMap.get(interviewStatus.getDepartmentId()));

                //筛选志愿状态
                String rounds;
                if (interviewStatus.getRound() == null) {
                    rounds = "未安排";
                } else if (interviewStatus.getRound() == 1) {
                    rounds = "一面";
                } else if (interviewStatus.getRound() == 2) {
                    rounds = "二面";
                } else if (interviewStatus.getRound() == 3) {
                    rounds = "三面";
                } else {
                    rounds = "四面";
                }
                Integer status = interviewStatus.getState();
                String interviewStatusNow;
                if (status == 9) {
                    interviewStatusNow = rounds + "通过";
                } else if (status == 8) {
                    interviewStatusNow = rounds + "失败";
                } else if (status == 6) {
                    interviewStatusNow = rounds + "进行中";
                } else {
                    interviewStatusNow = rounds + "未开始";
                }
                dataDashboardInfoPo.setVolunteerStatus(interviewStatusNow);

                if (interviewStatus.getStartTime() == null) {
                    dataDashboardInfoPo.setNextTime("--");
                } else {
                    dataDashboardInfoPo.setNextTime(TimeTransUtil.transStringToTimeDataDashboard(interviewStatus.getStartTime()));
                }

                if (addressMap.get(interviewStatus.getAdmissionAddressId()) == null) {
                    dataDashboardInfoPo.setNextPlace("--");
                } else {
                    dataDashboardInfoPo.setNextPlace(addressMap.get(interviewStatus.getAdmissionAddressId()));
                }
                interviewerInfoList.add(dataDashboardInfoPo);

            }



        DataDashboardInfoResult result = new DataDashboardInfoResult();

        result.setInterviewerInfoList(interviewerInfoList);
        result.setSiftBar(siftBarPo);
        result.setPageNow(page);

        int count = interviewStatusMapper.selectCount(new QueryWrapper<InterviewStatus>().select("id").eq("admission_id", admissionId)).intValue();

        if (count % pageNum != 0) {
            result.setPageNum( count / pageNum + 1);
        } else {
            result.setPageNum(count / pageNum);
        }

        return CommonResult.success(result);
    }

    @Override
    public CommonResult<DataDashboardInfoResult> sift(SiftParam siftParam, Integer page, Integer pageNum) {

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

        //从redis中获取数据面板导航栏
        SiftBarPo siftBarPo = redisUtil.getString("data,siftBar+" + admissionId, SiftBarPo.class);

//        Organization organization = organizationMapper.selectById(organizationId);
//
//        String organizationName = organization.getName();

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

        List<DataDashboardInfoPo> interviewerInfoList = new ArrayList<>();

        if (siftBarPo == null) {

            siftBarPo = new SiftBarPo();

//            Map<Integer, Integer> admissionOrderBarMap = new HashMap<>();
//
//            Map<Integer, Integer> departmentOrderBarMap = new HashMap<>();
//
//            Map<Integer, Integer> nowDepartmentBarMap = new HashMap<>();
//
//            Map<String, Integer> nextTimeBarMap = new HashMap<>();
//
//            Map<Integer, Integer> nextPlaceBarMap = new HashMap<>();


            List<SiftInfoPo> organizationOrderBar = new ArrayList<>();
            for (GroupIntCountPo organizationOrderGroupCountPo : myInterviewStatusMapper.selectOrganizationOrderCountByAdmissionId(admissionId)) {

                SiftInfoPo siftInfoPo = new SiftInfoPo();
                siftInfoPo.setInfo(organizationOrderGroupCountPo.getId());
                if (organizationOrderGroupCountPo.getId() == 1) {
                    siftInfoPo.setSiftName("第一志愿");
                } else if (organizationOrderGroupCountPo.getId() == 2) {
                    siftInfoPo.setSiftName("第二志愿");
                } else {
                    siftInfoPo.setSiftName("第三志愿");
                }
                siftInfoPo.setNumber(organizationOrderGroupCountPo.getCount());
                organizationOrderBar.add(siftInfoPo);
            }

            List<SiftInfoPo> departmentOrderBar = new ArrayList<>();
            for (GroupIntCountPo departmentOrderGroupCountPo : myInterviewStatusMapper.selectDepartmentOrderCountByAdmissionId(admissionId)) {

                SiftInfoPo siftInfoPo = new SiftInfoPo();
                siftInfoPo.setInfo(departmentOrderGroupCountPo.getId());
                if (departmentOrderGroupCountPo.getId() == 1) {
                    siftInfoPo.setSiftName("第一志愿");
                } else if (departmentOrderGroupCountPo.getId() == 2) {
                    siftInfoPo.setSiftName("第二志愿");
                } else {
                    siftInfoPo.setSiftName("第三志愿");
                }
                siftInfoPo.setNumber(departmentOrderGroupCountPo.getCount());
                departmentOrderBar.add(siftInfoPo);
            }

            List<SiftInfoPo> departmentBar = new ArrayList<>();
            for (GroupIntCountPo departmentGroupCountPo : myInterviewStatusMapper.selectDepartmentCountByAdmissionId(admissionId)) {

                SiftInfoPo siftInfoPo = new SiftInfoPo();
                siftInfoPo.setInfo(departmentGroupCountPo.getId());
                siftInfoPo.setSiftName(departmentMap.get(departmentGroupCountPo.getId()));
                siftInfoPo.setNumber(departmentGroupCountPo.getCount());
                departmentBar.add(siftInfoPo);
            }

            List<SiftInfoPo> nextTimeBar = new ArrayList<>();
            for (GroupLocalTimeCountPo nextTimeGroupCountPo : myInterviewStatusMapper.selectNextTimeCountByAdmissionId(admissionId)) {

                SiftInfoPo siftInfoPo = new SiftInfoPo();
//                siftInfoPo.setInfo(nextTimeGroupCountPo.getId());
                if (nextTimeGroupCountPo.getId() == null) {
                    siftInfoPo.setSiftName("--");
                } else {
                    siftInfoPo.setSiftName(TimeTransUtil.transStringToTimeDataDashboard(nextTimeGroupCountPo.getId()));
                }
                siftInfoPo.setNumber(nextTimeGroupCountPo.getCount());
                nextTimeBar.add(siftInfoPo);
            }

            List<SiftInfoPo> nextPlaceBar = new ArrayList<>();
            for (GroupIntCountPo PlaceGroupCountPo : myInterviewStatusMapper.selectAddressCountByAdmissionId(admissionId)) {

                SiftInfoPo siftInfoPo = new SiftInfoPo();
                siftInfoPo.setInfo(PlaceGroupCountPo.getId());
                if (PlaceGroupCountPo.getId() == null) {
                    siftInfoPo.setSiftName("--");
                } else {
                    siftInfoPo.setSiftName(addressMap.get(PlaceGroupCountPo.getId()));
                }
                siftInfoPo.setNumber(PlaceGroupCountPo.getCount());
                nextPlaceBar.add(siftInfoPo);
            }

            siftBarPo.setOrganizationOrderBar(organizationOrderBar);
            siftBarPo.setDepartmentOrderBar(departmentOrderBar);
            siftBarPo.setNowDepartmentBar(departmentBar);
            siftBarPo.setNextTimeBar(nextTimeBar);
            siftBarPo.setNextPlaceBar(nextPlaceBar);


            redisUtil.setString("data,siftBar+" + admissionId, siftBarPo, 5, TimeUnit.MINUTES);

        }

        if (siftParam.getInterviewStatusSift() != null) {
            List<Integer> interviewStatusSift = new ArrayList<>();
            for (Integer t : siftParam.getInterviewStatusSift()) {
                if (t == 1) {
                    interviewStatusSift.add(0);
                    interviewStatusSift.add(1);
                    interviewStatusSift.add(2);
                    interviewStatusSift.add(3);
                    interviewStatusSift.add(4);
                    interviewStatusSift.add(5);
                } else if (t == 2) {
                    interviewStatusSift.add(6);
                    interviewStatusSift.add(7);
                } else if (t == 3) {
                    interviewStatusSift.add(8);
                } else if (t == 4) {
                    interviewStatusSift.add(9);
                } else {
                    return CommonResult.fail("错误的面试状态参数");
                }
            }
            siftParam.setInterviewStatusSift(interviewStatusSift);
        }

        int placeFlag = 0;

        if (!siftParam.getNextPlaceSift().isEmpty()) {
            for (Integer t : siftParam.getNextPlaceSift()) {
                if (t == 0) {
                    placeFlag = 1;
                    break;
                }
            }
        } else {
            placeFlag = 1;
        }

        int timeFlag = 0;
        if (!siftParam.getNextTimeSift().isEmpty()) {
            for (String str : siftParam.getNextTimeSift()) {
                if (str.equals("--")) {
                    timeFlag = 1;
                    break;
                }
            }
        } else {
            timeFlag = 1;
        }

        int i = 0;

        int start = (page - 1) * pageNum;
        int end = page * pageNum;

        int num = 0;

        List<MyInterviewStatusPo> interviewStatuses = myInterviewStatusMapper.selectByAdmissionIdAndSift(siftParam, admissionId);

        for (MyInterviewStatusPo interviewStatus : interviewStatuses) {

//                admissionOrderBarMap.merge(interviewStatus.getOrganizationOrder(), 1, Integer::sum);
//                departmentOrderBarMap.merge(interviewStatus.getDepartmentOrder(), 1, Integer::sum);
//                nowDepartmentBarMap.merge(interviewStatus.getDepartmentId(), 1, Integer::sum);
//                nextTimeBarMap.merge(interviewStatus.getStartTime().toString(), 1, Integer::sum);
//                nextPlaceBarMap.merge(interviewStatus.getAdmissionAddressId(), 1, Integer::sum);

            String className = classMap.get(interviewStatus.getMajorClassId());

            if (
                    (siftParam.getSearch() != null &&
                            !(
                                    interviewStatus.getName().contains(siftParam.getSearch()) ||
                                    interviewStatus.getStudentId().contains(siftParam.getSearch()) ||
                                    className.contains(siftParam.getSearch()) ||
                                    interviewStatus.getPhone().contains(siftParam.getSearch())
                            )
                    )
            ) {
                ++num;
                continue;
            }

            if (i < end && i >= start) {

                if (placeFlag == 0 && interviewStatus.getAdmissionAddressId() == null) {
                    continue;
                }

                if (timeFlag == 0 && interviewStatus.getStartTime() == null) {
                    continue;
                }

                DataDashboardInfoPo dataDashboardInfoPo = new DataDashboardInfoPo();
                dataDashboardInfoPo.setId(interviewStatus.getId());
                dataDashboardInfoPo.setStudentId(interviewStatus.getStudentId());
                dataDashboardInfoPo.setName(interviewStatus.getName());
                dataDashboardInfoPo.setClassName(className);
                dataDashboardInfoPo.setPhone(interviewStatus.getPhone());

                if (interviewStatus.getOrganizationOrder() == 1) {
                    dataDashboardInfoPo.setOrganizationOrder("第一志愿");
                } else if (interviewStatus.getOrganizationOrder() == 2) {
                    dataDashboardInfoPo.setOrganizationOrder("第二志愿");
                } else {
                    dataDashboardInfoPo.setOrganizationOrder("第三志愿");
                }

                if (interviewStatus.getDepartmentOrder() == 1) {
                    dataDashboardInfoPo.setDepartmentOrder("第一志愿");
                } else if (interviewStatus.getDepartmentOrder() == 2) {
                    dataDashboardInfoPo.setDepartmentOrder("第二志愿");
                } else {
                    dataDashboardInfoPo.setDepartmentOrder("第三志愿");
                }

                dataDashboardInfoPo.setNowDepartment(departmentMap.get(interviewStatus.getDepartmentId()));

                //筛选志愿状态
                String rounds;
                if (interviewStatus.getRound() == null) {
                    rounds = "未安排";
                } else if (interviewStatus.getRound() == 1) {
                    rounds = "一面";
                } else if (interviewStatus.getRound() == 2) {
                    rounds = "二面";
                } else if (interviewStatus.getRound() == 3) {
                    rounds = "三面";
                } else {
                    rounds = "四面";
                }
                Integer status = interviewStatus.getState();
                String interviewStatusNow;
                if (status == 9) {
                    interviewStatusNow = rounds + "通过";
                } else if (status == 8) {
                    interviewStatusNow = rounds + "失败";
                } else if (status == 6) {
                    interviewStatusNow = rounds + "进行中";
                }
                else {
                    interviewStatusNow = rounds + "未开始";
                }
                dataDashboardInfoPo.setVolunteerStatus(interviewStatusNow);

                if (interviewStatus.getStartTime() == null) {
                    dataDashboardInfoPo.setNextTime("--");
                } else {
                    dataDashboardInfoPo.setNextTime(TimeTransUtil.transStringToTimeDataDashboard(interviewStatus.getStartTime()));
                }

                if (addressMap.get(interviewStatus.getAdmissionAddressId()) == null) {
                    dataDashboardInfoPo.setNextPlace("--");
                } else {
                    dataDashboardInfoPo.setNextPlace(addressMap.get(interviewStatus.getAdmissionAddressId()));
                }
                interviewerInfoList.add(dataDashboardInfoPo);


            }
            ++i;
        }

//        if (siftParam.getSort() != null) {
//            if (siftParam.getSort().getSortId() == 1) {
//                if (siftParam.getSort().getSortBy() == 1) {
//                    interviewerInfoList.sort(Comparator.comparing(DataDashboardInfoPo::getStudentId));
//                } else {
//                    interviewerInfoList.sort(Comparator.comparing(DataDashboardInfoPo::getStudentId).reversed());
//                }
//            } else if (siftParam.getSort().getSortId() == 2) {
//                if (siftParam.getSort().getSortBy() == 1) {
//                    interviewerInfoList.sort(Comparator.comparing(DataDashboardInfoPo::getName));
//                } else {
//                    interviewerInfoList.sort(Comparator.comparing(DataDashboardInfoPo::getName).reversed());
//                }
//            }
//        }

        DataDashboardInfoResult result = new DataDashboardInfoResult();

        result.setInterviewerInfoList(interviewerInfoList);
        result.setSiftBar(siftBarPo);
        result.setPageNow(page);

        int count = interviewStatuses.size() - num;
        if (count % pageNum != 0) {
            result.setPageNum( count / pageNum + 1);
        } else {
            result.setPageNum(count / pageNum);
        }

        return CommonResult.success(result);
    }

    @Override
    public CommonResult<DataDashboardExportResult> export(SiftParam siftParam) {

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


//        Organization organization = organizationMapper.selectById(organizationId);
//
//        String organizationName = organization.getName();

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

        List<DataDashboardInfoPo> interviewerInfoList = new ArrayList<>();

        int placeFlag = 0;
        int timeFlag = 0;

        List<MyInterviewStatusPo> interviewStatuses;
        if (siftParam == null) {
            interviewStatuses = myInterviewStatusMapper.selectByAdmissionIdAndUserInfo(admissionId);
        }
        else {
            if (siftParam.getInterviewStatusSift() != null) {
                List<Integer> interviewStatusSift = new ArrayList<>();
                for (Integer t : siftParam.getInterviewStatusSift()) {
                    if (t == 1) {
                        interviewStatusSift.add(0);
                        interviewStatusSift.add(1);
                        interviewStatusSift.add(2);
                        interviewStatusSift.add(3);
                        interviewStatusSift.add(4);
                        interviewStatusSift.add(5);
                    } else if (t == 2) {
                        interviewStatusSift.add(6);
                        interviewStatusSift.add(7);
                    } else if (t == 3) {
                        interviewStatusSift.add(8);
                    } else if (t == 4) {
                        interviewStatusSift.add(9);
                    } else {
                        return CommonResult.fail("错误的面试状态参数");
                    }
                }
                siftParam.setInterviewStatusSift(interviewStatusSift);
            }

            if (!siftParam.getNextPlaceSift().isEmpty()) {
                for (Integer t : siftParam.getNextPlaceSift()) {
                    if (t == 0) {
                        placeFlag = 1;
                        break;
                    }
                }
            } else {
                placeFlag = 1;
            }


            if (!siftParam.getNextTimeSift().isEmpty()) {
                for (String str : siftParam.getNextTimeSift()) {
                    if (str.equals("--")) {
                        timeFlag = 1;
                        break;
                    }
                }
            } else {
                timeFlag = 1;
            }



            interviewStatuses = myInterviewStatusMapper.selectByAdmissionIdAndSift(siftParam, admissionId);
        }

//        int i = 0;

        for (MyInterviewStatusPo interviewStatus : interviewStatuses) {

//                admissionOrderBarMap.merge(interviewStatus.getOrganizationOrder(), 1, Integer::sum);
//                departmentOrderBarMap.merge(interviewStatus.getDepartmentOrder(), 1, Integer::sum);
//                nowDepartmentBarMap.merge(interviewStatus.getDepartmentId(), 1, Integer::sum);
//                nextTimeBarMap.merge(interviewStatus.getStartTime().toString(), 1, Integer::sum);
//                nextPlaceBarMap.merge(interviewStatus.getAdmissionAddressId(), 1, Integer::sum);

            String className = classMap.get(interviewStatus.getMajorClassId());
//            if (
//                    siftParam != null && !(siftParam.getSearch() != null &&
//                            (className.contains(siftParam.getSearch()))
//                    )
//            )
            if (
                       siftParam!= null && (siftParam.getSearch() != null &&
                                !(
                                        interviewStatus.getName().contains(siftParam.getSearch()) ||
                                                interviewStatus.getStudentId().contains(siftParam.getSearch()) ||
                                                className.contains(siftParam.getSearch()) ||
                                                interviewStatus.getPhone().contains(siftParam.getSearch())
                                )
                        )
            ) {
                continue;
            }

            if (placeFlag == 0 && interviewStatus.getAdmissionAddressId() == null) {
                continue;
            }

            if (timeFlag == 0 && interviewStatus.getStartTime() == null) {
                continue;
            }

            DataDashboardInfoPo dataDashboardInfoPo = new DataDashboardInfoPo();
            dataDashboardInfoPo.setId(interviewStatus.getId());
            dataDashboardInfoPo.setStudentId(interviewStatus.getStudentId());
            dataDashboardInfoPo.setName(interviewStatus.getName());
            dataDashboardInfoPo.setClassName(className);
            dataDashboardInfoPo.setPhone(interviewStatus.getPhone());

            if (interviewStatus.getOrganizationOrder() == 1) {
                dataDashboardInfoPo.setOrganizationOrder("第一志愿");
            } else if (interviewStatus.getOrganizationOrder() == 2) {
                dataDashboardInfoPo.setOrganizationOrder("第二志愿");
            } else {
                dataDashboardInfoPo.setOrganizationOrder("第三志愿");
            }

            if (interviewStatus.getDepartmentOrder() == 1) {
                dataDashboardInfoPo.setDepartmentOrder("第一志愿");
            } else if (interviewStatus.getDepartmentOrder() == 2) {
                dataDashboardInfoPo.setDepartmentOrder("第二志愿");
            } else {
                dataDashboardInfoPo.setDepartmentOrder("第三志愿");
            }

            dataDashboardInfoPo.setNowDepartment(departmentMap.get(interviewStatus.getDepartmentId()));

            //筛选志愿状态
            String rounds;
            if (interviewStatus.getRound() == null) {
                rounds = "未安排";
            } else if (interviewStatus.getRound() == 1) {
                rounds = "一面";
            } else if (interviewStatus.getRound() == 2) {
                rounds = "二面";
            } else if (interviewStatus.getRound() == 3) {
                rounds = "三面";
            } else {
                rounds = "四面";
            }
            Integer status = interviewStatus.getState();
            String interviewStatusNow;
            if (status == 9) {
                interviewStatusNow = rounds + "通过";
            } else if (status == 8) {
                interviewStatusNow = rounds + "失败";
            } else {
                interviewStatusNow = rounds + "进行中";
            }
            dataDashboardInfoPo.setVolunteerStatus(interviewStatusNow);

            if (interviewStatus.getStartTime() == null) {
                dataDashboardInfoPo.setNextTime("--");
            } else {
                dataDashboardInfoPo.setNextTime(TimeTransUtil.transStringToTimeDataDashboard(interviewStatus.getStartTime()));
            }

            if (addressMap.get(interviewStatus.getAdmissionAddressId()) == null) {
                dataDashboardInfoPo.setNextPlace("--");
            } else {
                dataDashboardInfoPo.setNextPlace(addressMap.get(interviewStatus.getAdmissionAddressId()));
            }
            interviewerInfoList.add(dataDashboardInfoPo);

//            ++i;


        }

//        if (siftParam != null && siftParam.getSort() != null) {
//            if (siftParam.getSort().getSortId() == 1) {
//                if (siftParam.getSort().getSortBy() == 1) {
//                    interviewerInfoList.sort(Comparator.comparing(DataDashboardInfoPo::getStudentId));
//                } else {
//                    interviewerInfoList.sort(Comparator.comparing(DataDashboardInfoPo::getStudentId).reversed());
//                }
//            } else if (siftParam.getSort().getSortId() == 2) {
//                if (siftParam.getSort().getSortBy() == 1) {
//                    interviewerInfoList.sort(Comparator.comparing(DataDashboardInfoPo::getName));
//                } else {
//                    interviewerInfoList.sort(Comparator.comparing(DataDashboardInfoPo::getName).reversed());
//                }
//            }
//        }

        DataDashboardExportResult result = new DataDashboardExportResult();

        result.setInterviewerInfoList(interviewerInfoList);

        return CommonResult.success(result);
    }

    @Override
    public CommonResult<ResumeInfoResult> resume(Integer id) {

        /*
          鉴权并且获取用户所属社团组织id
         */
        BTokenSwapPo context = ThreadLocalContextUtil.getContext();
        Integer organizationId = context.getOrganizationId();

        InterviewStatus interviewStatus = interviewStatusMapper.selectById(id);

        //TODO:验证权限

        if (interviewStatus == null) {
            return CommonResult.fail("参数错误");
        }

        Admission admission = admissionMapper.selectOne(
                new QueryWrapper<Admission>()
                        .eq("organization_id", organizationId)
                        .orderByDesc("id")
        );

        if (admission == null || !Objects.equals(admission.getId(), interviewStatus.getAdmissionId())) {
            return CommonResult.fail();
        }

        UserInfo userInfo = userInfoMapper.selectOne(new QueryWrapper<UserInfo>().eq("id", interviewStatus.getUserId()).last("limit 1"));

        MajorClass majorClass = majorClassMapper.selectById(userInfo.getMajorClassId());
        if (majorClass == null) {
            return CommonResult.fail();
        }
        AcaMajor acaMajor = acaMajorMapper.selectById(majorClass.getMajorId());
        if (acaMajor == null) {
            return CommonResult.fail();
        }

        if (addressMap.isEmpty()) {
            for (AdmissionAddress admissionAddress : admissionAddressMapper.selectList(new QueryWrapper<>())) {
                addressMap.putIfAbsent(admissionAddress.getId(), admissionAddress.getName());
            }
        }

        /*
          获取报名基本问题
         */
        BasicQuestionPo basicQuestionPo = new BasicQuestionPo();
        for (AdmissionQuestion admissionQuestion : admissionQuestionMapper.selectList(
                new QueryWrapper<AdmissionQuestion>()
                        .eq("admission_id", interviewStatus.getAdmissionId())
                        .eq("question_type", 1)
        )
        ) {
            //name
            if (admissionQuestion.getQuestionId() == 1) {
                basicQuestionPo.setName(userInfo.getName());
            }
            //studentId
            else if (admissionQuestion.getQuestionId() == 2) {
                basicQuestionPo.setStudentId(userInfo.getStudentId());
            }
            //phone
            else if (admissionQuestion.getQuestionId() == 3) {
                basicQuestionPo.setPhone(userInfo.getPhone());
            }
            //gender
            else if (admissionQuestion.getQuestionId() == 4) {
                basicQuestionPo.setGender(userInfo.getGander() == 1 ? "男":"女");
            }
            //email
            else if (admissionQuestion.getQuestionId() == 5) {
                basicQuestionPo.setEmail(userInfo.getEmail());
            }
            //QQNumber
            else if (admissionQuestion.getQuestionId() == 6) {
                basicQuestionPo.setEmail(userInfo.getQq());
            }
            //academy
            else if (admissionQuestion.getQuestionId() == 7) {
                basicQuestionPo.setAcademy(acaMajor.getAcademy());
            }
            //major
            else if (admissionQuestion.getQuestionId() == 8) {
                basicQuestionPo.setMajor(acaMajor.getMajor());
            }
            //className
            else if (admissionQuestion.getQuestionId() == 9) {
                basicQuestionPo.setClassName(majorClass.getClassNum());
            }

        }

        /*
          获取部门基本问题
         */
        DepartmentQuestionPo departmentQuestionPo = new DepartmentQuestionPo();

        Department department = departmentMapper.selectById(interviewStatus.getDepartmentId());

        departmentQuestionPo.setDepartmentName(department.getName());
        UserVolunteer userVolunteer = userVolunteerMapper.selectOne(
                new QueryWrapper<UserVolunteer>()
                        .eq("admission_id", interviewStatus.getAdmissionId())
                        .eq("department_id", interviewStatus.getDepartmentId())
                        .eq("user_id", interviewStatus.getUserId())
                        .last("limit 1")
        );
        if (userVolunteer != null) {
            departmentQuestionPo.setIsTransfers(userVolunteer.getIsTransfers() == 1);
        } else {
            log.warn("userVolunteer数据缺少，admission_id:{}, department_id:{}, user_id:{}",
                    interviewStatus.getAdmissionId(), interviewStatus.getDepartmentId(), interviewStatus.getUserId());
        }


        List<QuestionAnswerPo> departmentQuestionAnswerPoList = new ArrayList<>();
        for (AdmissionQuestion admissionQuestion : admissionQuestionMapper.selectList(
                new QueryWrapper<AdmissionQuestion>()
                        .eq("admission_id", interviewStatus.getAdmissionId())
                        .eq("department_id", interviewStatus.getDepartmentId())
                        .eq("question_type", 2)
        )
        ) {
            QuestionData questionData = questionDataMapper.selectById(admissionQuestion.getQuestionId());
            RegistrationFromData registrationFromData = registrationFromDataMapper.selectOne(
                    new QueryWrapper<RegistrationFromData>()
                            .eq("user_id", interviewStatus.getUserId())
                            .eq("admission_question_id", admissionQuestion.getId())
            );

            if (registrationFromData != null) {
                QuestionAnswerPo questionAnswerPo = new QuestionAnswerPo();
                questionAnswerPo.setQuestionName(questionData.getQuestion());
                questionAnswerPo.setAnswer(registrationFromData.getData());
                questionAnswerPo.setOrder(admissionQuestion.getOrder());
                departmentQuestionAnswerPoList.add(questionAnswerPo);
            } else {
                log.warn("registrationFromData数据缺少，user_id:{}, admission_question_id:{}",
                        interviewStatus.getUserId(), admissionQuestion.getId());
            }

        }

        departmentQuestionPo.setQuestionList(departmentQuestionAnswerPoList);

        /*
          获取综合问题
         */
        ComprehensiveQuestionPo comprehensiveQuestion = new ComprehensiveQuestionPo();

        List<QuestionAnswerPo> comprehensiveQuestionAnswerPoList = new ArrayList<>();
        for (AdmissionQuestion admissionQuestion : admissionQuestionMapper.selectList(
                new QueryWrapper<AdmissionQuestion>()
                        .eq("admission_id", interviewStatus.getAdmissionId())
                        .eq("question_type", 3)
        )
        ) {
            QuestionData questionData = questionDataMapper.selectById(admissionQuestion.getQuestionId());
            RegistrationFromData registrationFromData = registrationFromDataMapper.selectOne(
                    new QueryWrapper<RegistrationFromData>()
                            .eq("user_id", interviewStatus.getUserId())
                            .eq("admission_question_id", admissionQuestion.getId())
            );

            if (registrationFromData != null) {
                QuestionAnswerPo questionAnswerPo = new QuestionAnswerPo();
                questionAnswerPo.setQuestionName(questionData.getQuestion());
                questionAnswerPo.setAnswer(registrationFromData.getData());
                questionAnswerPo.setOrder(admissionQuestion.getOrder());

                comprehensiveQuestionAnswerPoList.add(questionAnswerPo);
            } else {
                log.warn("registrationFromData数据缺少，user_id:{}, admission_question_id:{}",
                        interviewStatus.getUserId(), admissionQuestion.getId());
            }
        }
        comprehensiveQuestion.setQuestionList(comprehensiveQuestionAnswerPoList);

        /*
          获取面试反馈
         */
        List<InterviewFeedbackPo> interviewFeedbackList = new ArrayList<>();

        for (Message message : messageMapper.selectList(
                new QueryWrapper<Message>()
                        .eq("user_id", userInfo.getId())
                        .eq("interview_status_id", interviewStatus.getId())
        )) {
            InterviewFeedbackPo interviewFeedbackPo = new InterviewFeedbackPo();

            interviewFeedbackPo.setTime(TimeTransUtil.tranStringToTimeNotS(message.getTime()));
            if (message.getState() == 1) {
                interviewFeedbackPo.setState("接受");
            } else if (message.getState() == 2) {
                interviewFeedbackPo.setState("拒绝");
            } else {
                interviewFeedbackPo.setState("待定");
            }

            interviewFeedbackList.add(interviewFeedbackPo);
        }

        /*
          获取面试签到
         */
        InterviewFeedbackPo signIn = new InterviewFeedbackPo();
        if (interviewStatus.getSignInTime() != null) {
            signIn.setTime(TimeTransUtil.tranStringToTimeNotS(interviewStatus.getSignInTime()));
            signIn.setState("完成签到");
        } else {
            if (interviewStatus.getStartTime() != null) {
                signIn.setTime(TimeTransUtil.tranStringToTimeNotS(interviewStatus.getStartTime()));
            } else {
                signIn.setTime("--");
            }
            signIn.setState("未完成签到");
        }

        /*
          获取面试安排
         */
        List<InterviewArrangementPo> interviewArrangementPos = new ArrayList<>();

        for (InterviewStatus status : interviewStatusMapper.selectList(
                new QueryWrapper<InterviewStatus>()
                        .eq("user_id", interviewStatus.getUserId())
                        .eq("admission_id", interviewStatus.getAdmissionId())
                        .eq("department_id", interviewStatus.getDepartmentId())
                        .orderByAsc("round")
        )) {
            InterviewArrangementPo interviewArrangementPo = new InterviewArrangementPo();
            interviewArrangementPo.setRound(status.getRound());

            if (status.getStartTime() != null) {
                interviewArrangementPo.setTime(TimeTransUtil.tranStringToTimeNotS(status.getStartTime()));
            } else {
                interviewArrangementPo.setTime("--");
            }

            if (status.getAdmissionAddressId() != null) {
                AdmissionAddress admissionAddress = admissionAddressMapper.selectById(status.getAdmissionAddressId());
                interviewArrangementPo.setTime(admissionAddress.getName());
            } else {
                interviewArrangementPo.setTime("--");
            }

            interviewArrangementPo.setPlace(addressMap.get(status.getAdmissionAddressId()));

            interviewArrangementPos.add(interviewArrangementPo);
        }

        ResumeInfoResult result = new ResumeInfoResult();
        result.setBasicQuestion(basicQuestionPo);
        result.setDepartmentQuestion(departmentQuestionPo);
        result.setComprehensiveQuestion(comprehensiveQuestion);
        result.setInterviewFeedbackList(interviewFeedbackList);
        result.setSignIn(signIn);
        result.setInterviewArrangementList(interviewArrangementPos);

        return CommonResult.success(result);
    }

    @Override
    public CommonResult<EvaluationInfoResult> evaluationInfo(Integer id, Integer round) {

        InterviewStatus interviewStatus = interviewStatusMapper.selectById(id);

        if (interviewStatus == null) {
            return CommonResult.fail();
        }

        /*
          鉴权并且获取用户所属社团组织id
         */
        BTokenSwapPo context = ThreadLocalContextUtil.getContext();
        Integer organizationId = context.getOrganizationId();

        Admission admission = admissionMapper.selectOne(
                new QueryWrapper<Admission>()
                        .eq("organization_id", organizationId)
                        .orderByDesc("id")
        );

        if (admission == null || !Objects.equals(admission.getId(), interviewStatus.getAdmissionId())) {
            return CommonResult.fail();
        }

        if (round != null && !Objects.equals(round, interviewStatus.getRound())) {
            interviewStatus = interviewStatusMapper.selectOne(
                    new QueryWrapper<InterviewStatus>()
                            .eq("user_id", interviewStatus.getUserId())
                            .eq("admission_id", interviewStatus.getAdmissionId())
                            .eq("department_id", interviewStatus.getDepartmentId())
                            .eq("round", round)
            );
        }

        if (interviewStatus == null) {
            return CommonResult.fail();
        }

        if (departmentMap.isEmpty()) {
            for (Department department : departmentMapper.selectList(new QueryWrapper<>())) {
                departmentMap.putIfAbsent(department.getId(), department.getName());
            }
        }

        EvaluationInfoResult result = new EvaluationInfoResult();
        if (interviewStatus.getState() == 9) {
            result.setStatus(1);
        } else if (interviewStatus.getState() == 8 || interviewStatus.getState() == 0) {
            result.setStatus(2);
        } else {
            result.setStatus(3);
        }

        result.setRound(interviewStatus.getRound());

        List<InterviewerOpinionPo> interviewResult = new ArrayList<>();

        List<InterviewerOpinionPo> passResult = new ArrayList<>();

        List<InterviewerOpinionPo> comprehensiveQuestionList = new ArrayList<>();


        /*
          获取面试分数及排名
         */
        InterviewGradingPo interviewGradingPo = new InterviewGradingPo();

//        interviewGradingPo.setRank(redisUtil.zSetScore("admission:" + interviewStatus.getAdmissionId() + ",department:" + interviewStatus.getDepartmentId() + ",round" + round,
//                id).intValue());

        if (interviewStatus.getState() == 9 || interviewStatus.getState() == 8 || interviewStatus.getState() == 7) {

            //面试评价
            List<InterviewEvaluation> interviewEvaluationList = interviewEvaluationMapper.selectList(
                    new QueryWrapper<InterviewEvaluation>()
                            .eq("interview_status_id", id)
            );

            List<QuestionScorePo> questionPoList = new ArrayList<>();
            if (!interviewEvaluationList.isEmpty()) {
                int size = interviewEvaluationList.size() + 1;

                List<String> userBList = new ArrayList<>();
                for (InterviewEvaluation interviewEvaluation : interviewEvaluationList) {

                    UserB userB = userBMapper.selectById(interviewEvaluation.getUserBId());

                    userBList.add(userB.getUserName());

                    //
                    InterviewerOpinionPo interviewerOpinionPo1 = new InterviewerOpinionPo();
                    interviewerOpinionPo1.setName(userB.getUserName());
                    if (interviewEvaluation.getIsPass() == 1) {
                        interviewerOpinionPo1.setOpinion("通过");
                    } else if (interviewEvaluation.getIsPass() == 2) {
                        interviewerOpinionPo1.setOpinion("失败");
                    } else {
                        interviewerOpinionPo1.setOpinion("待定");
                    }
                    interviewResult.add(interviewerOpinionPo1);

                    //
                    InterviewerOpinionPo interviewerOpinionPo2 = new InterviewerOpinionPo();
                    interviewerOpinionPo2.setName(userB.getUserName());
                    interviewerOpinionPo2.setOpinion(departmentMap.get(interviewEvaluation.getPassDepartmentId()));
                    passResult.add(interviewerOpinionPo2);

                    //
                    InterviewerOpinionPo interviewerOpinionPo3 = new InterviewerOpinionPo();
                    interviewerOpinionPo3.setName(userB.getUserName());
                    interviewerOpinionPo3.setOpinion(interviewEvaluation.getEvaluation());
                    comprehensiveQuestionList.add(interviewerOpinionPo3);

                }

                interviewGradingPo.setRank(1);

                Interviewer interviewer = new Interviewer();
                interviewer.setProject("面试官");
                interviewer.setName(userBList);
                interviewGradingPo.setInterviewer(interviewer);



                List<InterviewQuestion> interviewQuestions = interviewQuestionMapper.selectList(
                        new QueryWrapper<InterviewQuestion>()
                                .eq("admission_id", interviewStatus.getAdmissionId())
                                .eq("type", 6)
                );

                int[] arr = new int[size];

                QuestionScorePo questionScorePoT = new QuestionScorePo();
                questionPoList.add(questionScorePoT);


                for (InterviewQuestion interviewQuestion : interviewQuestions) {

                    QuestionScorePo questionScorePo = new QuestionScorePo();
                    QuestionData questionData = questionDataMapper.selectById(interviewQuestion.getQuestionId());

                    questionScorePo.setQuestion(questionData.getQuestion());

                    int i = 0;
                    int sum = 0;

                    List<Double> score = new ArrayList<>();
                    for (QuestionScore questionScore : questionScoreMapper.selectList(
                            new QueryWrapper<QuestionScore>()
                                    .eq("interview_status_id", id)
                                    .eq("interview_question_id", interviewQuestion.getId())
                                    .orderByAsc("user_b_id")
                    )) {
                        score.add(Double.valueOf(questionScore.getScore()));
                        arr[i] += questionScore.getScore();
                        sum += questionScore.getScore();
                        ++i;
                    }

                    score.add((double) (sum)/i);

                    arr[size -1] += sum/i;

                    questionScorePo.setScore(score);
                    questionPoList.add(questionScorePo);

                }

                questionScorePoT.setQuestion("总分");
                List<Double> score = new ArrayList<>();
                for (int i = 0; i < size; ++i) {
                    score.add((double) arr[i]);
                }
                questionScorePoT.setScore(score);
                questionPoList.set(0, questionScorePoT);

            }
            interviewGradingPo.setQuestionPoList(questionPoList);

            result.setIsTransfers(interviewStatus.getIsTransfers() == 1);

        }

        result.setInterviewResult(interviewResult);
        result.setPassResult(passResult);
        result.setComprehensiveQuestionList(comprehensiveQuestionList);

        if (interviewStatus.getState() == 9) {
            Department department = departmentMapper.selectById(interviewStatus.getDepartmentId());
            result.setPassDepartment(department.getName());
        }

        result.setInterviewGradingPo(interviewGradingPo);

        return CommonResult.success(result);
    }

    @Override
    public CommonResult<String> changeEvaluation(EvaluationChangeParam evaluationChangeParam) {

        InterviewStatus interviewStatus = interviewStatusMapper.selectById(evaluationChangeParam.getId());

        if (interviewStatus == null) {
            return CommonResult.fail("参数id不存在");
        }

        /*
          鉴权并且获取用户所属社团组织id
         */
        BTokenSwapPo context = ThreadLocalContextUtil.getContext();
        Integer organizationId = context.getOrganizationId();

        Admission admission = admissionMapper.selectOne(
                new QueryWrapper<Admission>()
                        .eq("organization_id", organizationId)
                        .orderByDesc("id")
        );

        if (admission == null || !Objects.equals(admission.getId(), interviewStatus.getAdmissionId())) {
            return CommonResult.fail("请求id不属于该社团");
        }

        if (evaluationChangeParam.getRound() != null && !Objects.equals(evaluationChangeParam.getRound(), interviewStatus.getRound())) {
            interviewStatus = interviewStatusMapper.selectOne(
                    new QueryWrapper<InterviewStatus>()
                            .eq("user_id", interviewStatus.getUserId())
                            .eq("admission_id", interviewStatus.getAdmissionId())
                            .eq("department_id", interviewStatus.getDepartmentId())
                            .eq("round", evaluationChangeParam.getRound())
            );
        }

        if (interviewStatus == null) {
            return CommonResult.fail("请求id不属于该社团");
        }

        if (evaluationChangeParam.getState() == 1) {
            interviewStatus.setState(9);
        } else if (evaluationChangeParam.getState() == 2) {
            interviewStatus.setState(8);
        } else {
            interviewStatus.setState(7);
        }

        int updateNum = interviewStatusMapper.updateById(interviewStatus);
        if (updateNum != 1) {
            log.error("数据面板更改面试评价接口异常，数据库更新数异常，updateNum:{}", updateNum);
        }

        return CommonResult.success("更改成功");
    }

    @Override
    public CommonResult<RoundResult> round(Integer id) {

        /*
          鉴权并且获取用户所属社团组织id
         */
        BTokenSwapPo context = ThreadLocalContextUtil.getContext();
        Integer organizationId = context.getOrganizationId();

//        Admission admission = admissionMapper.selectOne(
//                new QueryWrapper<Admission>()
//                        .eq("organization_id", organizationId)
//                        .orderByDesc("id")
//        );

        InterviewStatus interviewStatus = interviewStatusMapper.selectById(id);

        InterviewStatus interviewStatus1 = interviewStatusMapper.selectOne(
                new QueryWrapper<InterviewStatus>()
                        .eq("user_id", interviewStatus.getUserId())
                        .eq("admission_id", interviewStatus.getAdmissionId())
                        .eq("department_id", interviewStatus.getDepartmentId())
                        .orderByDesc("round")
                        .last("limit 1")
        );
        RoundResult result = new RoundResult();
        result.setRound(interviewStatus1.getRound());

        return CommonResult.success(result);
    }
}

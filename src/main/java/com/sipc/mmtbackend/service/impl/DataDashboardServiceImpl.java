package com.sipc.mmtbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sipc.mmtbackend.mapper.*;
import com.sipc.mmtbackend.mapper.customization.MyInterviewStatusMapper;
import com.sipc.mmtbackend.mapper.customization.MyMajorClassMapper;
import com.sipc.mmtbackend.pojo.domain.Admission;
import com.sipc.mmtbackend.pojo.domain.AdmissionAddress;
import com.sipc.mmtbackend.pojo.domain.Department;
import com.sipc.mmtbackend.pojo.domain.InterviewStatus;
import com.sipc.mmtbackend.pojo.domain.po.GroupIntCountPo;
import com.sipc.mmtbackend.pojo.domain.po.GroupLocalTimeCountPo;
import com.sipc.mmtbackend.pojo.domain.po.MajorClassPo;
import com.sipc.mmtbackend.pojo.domain.po.MyInterviewStatusPo;
import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.pojo.dto.param.dataDashboard.SiftParam;
import com.sipc.mmtbackend.pojo.dto.result.DataDashboardExportResult;
import com.sipc.mmtbackend.pojo.dto.result.dataDashboard.DataDashboardInfoResult;
import com.sipc.mmtbackend.pojo.dto.result.dataDashboard.po.DataDashboardInfoPo;
import com.sipc.mmtbackend.pojo.dto.result.dataDashboard.po.SiftBarPo;
import com.sipc.mmtbackend.pojo.dto.result.dataDashboard.po.SiftInfoPo;
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
        SiftBarPo siftBarPo = redisUtil.getString("siftBar+" + admissionId, SiftBarPo.class);

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


            redisUtil.setString("siftBar+" + admissionId, siftBarPo, 5, TimeUnit.MINUTES);

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
        SiftBarPo siftBarPo = redisUtil.getString("siftBar+" + admissionId, SiftBarPo.class);

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


            redisUtil.setString("siftBar+" + admissionId, siftBarPo, 5, TimeUnit.MINUTES);

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

        if (siftParam.getNextPlaceSift() != null) {
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
        if (siftParam.getNextTimeSift() != null) {
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

            if (siftParam.getNextPlaceSift() != null) {
                for (Integer t : siftParam.getNextPlaceSift()) {
                    if (t == 0) {
                        placeFlag = 1;
                        break;
                    }
                }
            } else {
                placeFlag = 1;
            }


            if (siftParam.getNextTimeSift() != null) {
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
}
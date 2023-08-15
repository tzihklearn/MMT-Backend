package com.sipc.mmtbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sipc.mmtbackend.mapper.*;
import com.sipc.mmtbackend.mapper.customization.MyInterviewStatusMapper;
import com.sipc.mmtbackend.pojo.domain.*;
import com.sipc.mmtbackend.pojo.domain.po.GroupIntCountPo;
import com.sipc.mmtbackend.pojo.domain.po.GroupLocalTimeCountPo;
import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.pojo.dto.param.dataDashboard.SiftParam;
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

import java.time.LocalDateTime;
import java.util.*;

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

    private final RedisUtil redisUtil;

    private Map<Integer, String> departmentMap = new HashMap<>();

    private Map<Integer, String> addressMap = new HashMap<>();

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


            redisUtil.setString("siftBar+" + admissionId, siftBarPo);

        }

        int i = 0;

        int start = (page -1) * pageNum + 1;
        int end = page * pageNum;


        i = start;

        for (InterviewStatus interviewStatus : interviewStatusMapper.selectList(new QueryWrapper<InterviewStatus>().eq("admission_id", admissionId).last("limit " + start + " , " + end))) {

//                admissionOrderBarMap.merge(interviewStatus.getOrganizationOrder(), 1, Integer::sum);
//                departmentOrderBarMap.merge(interviewStatus.getDepartmentOrder(), 1, Integer::sum);
//                nowDepartmentBarMap.merge(interviewStatus.getDepartmentId(), 1, Integer::sum);
//                nextTimeBarMap.merge(interviewStatus.getStartTime().toString(), 1, Integer::sum);
//                nextPlaceBarMap.merge(interviewStatus.getAdmissionAddressId(), 1, Integer::sum);

            ++i;

            if (i <= end && i >= start) {
                DataDashboardInfoPo dataDashboardInfoPo = new DataDashboardInfoPo();
                dataDashboardInfoPo.setId(interviewStatus.getId());
                dataDashboardInfoPo.setStudentId(Integer.valueOf(20230000 + i).toString());
                dataDashboardInfoPo.setName("测试用户" + i);
                dataDashboardInfoPo.setClassName("测试班级" + i % 5);
                dataDashboardInfoPo.setPhone(Integer.valueOf(1000011000 + i).toString());

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

        }

        DataDashboardInfoResult result = new DataDashboardInfoResult();

        result.setInterviewerInfoList(interviewerInfoList);
        result.setSiftBar(siftBarPo);
        result.setPageNow(page);
        result.setPageNum(interviewStatusMapper.selectCount(new QueryWrapper<InterviewStatus>().select("id")).intValue() / pageNum + 1);

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


            redisUtil.setString("siftBar+" + admissionId, siftBarPo);

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
                } else if (t ==4) {
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

        int start = (page -1) * pageNum + 1;
        int end = page * pageNum;


        i = start;
        List<InterviewStatus> interviewStatuses = myInterviewStatusMapper.selectByAdmissionIdAndSift(siftParam, admissionId);

        for (InterviewStatus interviewStatus : interviewStatuses) {

//                admissionOrderBarMap.merge(interviewStatus.getOrganizationOrder(), 1, Integer::sum);
//                departmentOrderBarMap.merge(interviewStatus.getDepartmentOrder(), 1, Integer::sum);
//                nowDepartmentBarMap.merge(interviewStatus.getDepartmentId(), 1, Integer::sum);
//                nextTimeBarMap.merge(interviewStatus.getStartTime().toString(), 1, Integer::sum);
//                nextPlaceBarMap.merge(interviewStatus.getAdmissionAddressId(), 1, Integer::sum);


            if (i < end && i >= start) {

                if (
                        !(siftParam.getSearch() != null &&
                        (Integer.valueOf(20230000 + i).toString().contains(siftParam.getSearch())
                                || ("测试用户" + i).contains(siftParam.getSearch())
                                || ("测试班级" + i % 5).contains(siftParam.getSearch())
                                || (Integer.valueOf(1000011000 + i).toString()).contains(siftParam.getSearch())
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
                dataDashboardInfoPo.setStudentId(Integer.valueOf(20230000 + i).toString());
                dataDashboardInfoPo.setName("测试用户" + i);
                dataDashboardInfoPo.setClassName("测试班级" + i % 5);
                dataDashboardInfoPo.setPhone(Integer.valueOf(1000011000 + i).toString());

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

                ++i;
            }

        }

        if (siftParam.getSort() != null) {
            if (siftParam.getSort().getSortId() == 1) {
                if (siftParam.getSort().getSortBy() == 1) {
                    interviewerInfoList.sort(Comparator.comparing(DataDashboardInfoPo::getStudentId));
                } else {
                    interviewerInfoList.sort(Comparator.comparing(DataDashboardInfoPo::getStudentId).reversed());
                }
            } else if (siftParam.getSort().getSortId() == 2) {
                if (siftParam.getSort().getSortBy() == 1) {
                    interviewerInfoList.sort(Comparator.comparing(DataDashboardInfoPo::getName));
                } else {
                    interviewerInfoList.sort(Comparator.comparing(DataDashboardInfoPo::getName).reversed());
                }
            }
        }

        DataDashboardInfoResult result = new DataDashboardInfoResult();

        result.setInterviewerInfoList(interviewerInfoList);
        result.setSiftBar(siftBarPo);
        result.setPageNow(page);
        result.setPageNum(interviewStatuses.size() / pageNum + 1);

        return CommonResult.success(result);
    }
}

package com.sipc.mmtbackend.service.c.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sipc.mmtbackend.mapper.*;
import com.sipc.mmtbackend.pojo.c.param.ShowOrganizationData.OrganizationOrDepartmentIntroduceData;
import com.sipc.mmtbackend.pojo.c.result.ShowOrganization.DepartmentAdmissionResult;
import com.sipc.mmtbackend.pojo.c.result.ShowOrganization.DepartmentIntroduceResult;
import com.sipc.mmtbackend.pojo.c.result.ShowOrganization.OrganizationIntroduceResult;
import com.sipc.mmtbackend.pojo.domain.Admission;
import com.sipc.mmtbackend.pojo.domain.Department;
import com.sipc.mmtbackend.pojo.domain.Organization;
import com.sipc.mmtbackend.pojo.domain.OrganizationRecruit;
import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.service.c.CacheService;
import com.sipc.mmtbackend.service.c.ShowOrganizationService;
import com.sipc.mmtbackend.utils.PictureUtil.PictureUtil;
import com.sipc.mmtbackend.utils.PictureUtil.pojo.DefaultPictureIdEnum;
import com.sipc.mmtbackend.utils.time.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Service
public class ShowOrganizationServiceImpl implements ShowOrganizationService {

    private final AdmissionMapper admissionMapper;
    private final OrganizationMapper organizationMapper;
    private final OrganizationRecruitMapper organizationRecruitMapper;
    private final AdmissionDepartmentMergeMapper admissionDepartmentMergeMapper;
    private final DepartmentMapper departmentMapper;
    private final OrganizationDepartmentMergeMapper organizationDepartmentMergeMapper;

    private final PictureUtil pictureUtil;

    @Resource
    private CacheService cacheService;

    @Autowired
    public ShowOrganizationServiceImpl(AdmissionMapper admissionMapper, OrganizationMapper organizationMapper,
                                       OrganizationRecruitMapper organizationRecruitMapper,
                                       AdmissionDepartmentMergeMapper admissionDepartmentMergeMapper,
                                       DepartmentMapper departmentMapper,
                                       OrganizationDepartmentMergeMapper organizationDepartmentMergeMapper, PictureUtil pictureUtil) {
        this.admissionMapper = admissionMapper;
        this.organizationMapper = organizationMapper;
        this.organizationRecruitMapper = organizationRecruitMapper;
        this.admissionDepartmentMergeMapper = admissionDepartmentMergeMapper;
        this.departmentMapper = departmentMapper;
        this.organizationDepartmentMergeMapper = organizationDepartmentMergeMapper;
        this.pictureUtil = pictureUtil;
    }

    @Override
    @Cacheable(value = "getOrganizationIntroduce", key = "#root.args")
    public CommonResult<OrganizationIntroduceResult> getOrganizationIntroduce(Integer admissionId, Integer organizationId) {

        //验证社团在纳新
//        List<Admission> admissions = admissionMapper.selectByAdmissionIdAndOrganization(admissionId, organizationId);
        Admission admission = admissionMapper.selectById(admissionId);

//        Admission admission = cacheService.getAdmissionCache(admissionId);
//        if (admissions.isEmpty()) {
//            return CommonResult.fail("A0400","该社团未在纳新");
//        };
        OrganizationIntroduceResult organizationIntroduceResult = new OrganizationIntroduceResult();

        //获取报名时间
//        Organization organization = cacheService.getOrganizationMapperCache(organizationId);

        Organization organization = organizationMapper.selectById(organizationId);
        if (organization == null) {
            return CommonResult.fail("没有该社团");
        }

        //获取标题和头像链接和社团名称
        String title = "详情" + "-" + organization.getName();


        String avatarUrl;

//        if (organization.getAvatarId() == null || organization.getAvatarId().isEmpty()) {
//            avatarUrl = pictureUtil.getPictureURL(DefaultPictureIdEnum.ORG_AVATAR.getPictureId(), true);
//        } else {
//            avatarUrl = pictureUtil.getPictureURL(organization.getAvatarId(), false);
//        }

        //TODO:更改图像链接
        avatarUrl = organization.getAvatarId();

        String name = organization.getName();

//        OrganizationRecruit organizationRecruit = cacheService.getOrganizationRecruit(organizationId);

        OrganizationRecruit organizationRecruit = organizationRecruitMapper.selectOne(new QueryWrapper<OrganizationRecruit>().eq("organization_id", organizationId));

        if (organizationRecruit == null) {
            return CommonResult.success(organizationIntroduceResult);
        }

        List<OrganizationOrDepartmentIntroduceData> organizationIntroduceDataList = new ArrayList<>();
        if (organizationRecruit.getDescription() != null) {
            organizationIntroduceDataList.add(new OrganizationOrDepartmentIntroduceData("社团介绍", organizationRecruit.getDescription()));
        }
        if (organizationRecruit.getFeature() != null) {
            organizationIntroduceDataList.add(new OrganizationOrDepartmentIntroduceData("社团特色", organizationRecruit.getFeature()));
        }
        if (organizationRecruit.getDaily() != null) {
            organizationIntroduceDataList.add(new OrganizationOrDepartmentIntroduceData("社团日常", organizationRecruit.getDaily()));
        }
        if (organizationRecruit.getSlogan() != null) {
            organizationIntroduceDataList.add(new OrganizationOrDepartmentIntroduceData("纳新宣言", organizationRecruit.getSlogan()));
        }
        if (organizationRecruit.getContactInfo() != null){
            organizationIntroduceDataList.add(new OrganizationOrDepartmentIntroduceData("联系方式", organizationRecruit.getContactInfo()));
        }
        if (organizationRecruit.getMore() != null) {
            organizationIntroduceDataList.add(new OrganizationOrDepartmentIntroduceData("更多", organizationRecruit.getMore()));
        }

        String registrationTime;
        if (admission == null) {
            registrationTime = "未安排";
            organizationIntroduceResult.setStartTime(null);
            organizationIntroduceResult.setEndTime(null);
        }
        else if (admission.getStartTime() == null || admission.getEndTime() == null) {
            registrationTime = "未安排";
            organizationIntroduceResult.setStartTime(null);
            organizationIntroduceResult.setEndTime(null);
            organizationIntroduceResult.setAllowDepartmentAmount(admission.getAllowDepartmentAmount());
        }
        else {
            String startTime = TimeUtil.transformYearAndMonthAdnDay(admission.getStartTime().toEpochSecond(ZoneOffset.of("+8")));
            String endTime = TimeUtil.transformYearAndMonthAdnDay(admission.getEndTime().toEpochSecond(ZoneOffset.of("+8")));
            registrationTime = startTime + "-" + endTime;

            organizationIntroduceResult.setStartTime(admission.getStartTime().toString());
            organizationIntroduceResult.setEndTime(admission.getEndTime().toString());
            organizationIntroduceResult.setAllowDepartmentAmount(admission.getAllowDepartmentAmount());
        }

        organizationIntroduceResult.setTitle(title);
        organizationIntroduceResult.setAvatarUrl(avatarUrl);
        organizationIntroduceResult.setName(name);

        organizationIntroduceResult.setRegistrationTime(registrationTime);
        organizationIntroduceResult.setOrganizationIntroduceDataList(organizationIntroduceDataList);

        return CommonResult.success(organizationIntroduceResult);

    }

    @Override
    @Cacheable(value = "getDepartmentAdmission", key = "#root.args")
    public CommonResult<List<DepartmentAdmissionResult>> getDepartmentAdmission(Integer admissionId, Integer organizationId) {

        List<DepartmentAdmissionResult> departmentAdmissionResultList = new ArrayList<>();

        List<Integer> departmentIds = organizationDepartmentMergeMapper.selectDepartmentIdByAdmissionId(admissionId);
//        List<Integer> departmentIds = cacheService.getDepartmentIdByOrganizationId(organizationId);

        for (Integer departmentId : departmentIds) {
//            Department department = cacheService.getDepartment(departmentId);
            Department department = departmentMapper.selectById(departmentId);
            if (department == null) {
                continue;
            }
//            Department department = departmentMapper.selectByPrimaryKey(departmentId);
            departmentAdmissionResultList.add(new DepartmentAdmissionResult(departmentId, department.getName(),
                    department.getBriefDescription()));
        }

        return CommonResult.success(departmentAdmissionResultList);
    }

    @Override
    @Cacheable(value = "getDepartmentIntroduce", key = "#root.args")
    public CommonResult<DepartmentIntroduceResult> getDepartmentIntroduce(Integer admissionId, Integer organizationId, Integer departmentId) {
        //验证社团在纳新
//        Admission admission = cacheService.getAdmissionCache(admissionId);
        Admission admission = admissionMapper.selectById(admissionId);

        DepartmentIntroduceResult departmentIntroduceResult = new DepartmentIntroduceResult();
        List<OrganizationOrDepartmentIntroduceData> departmentIntroduceDataList = new ArrayList<>();

        //获取报名时间
        String registrationTime;
        if (admission == null) {
            registrationTime = "未安排";
        }
        else if (admission.getStartTime() == null || admission.getEndTime() == null) {
            registrationTime = "未安排";
        }
        else {
            String startTime = TimeUtil.transformYearAndMonthAdnDay(admission.getStartTime().toEpochSecond(ZoneOffset.of("+8")));
            String endTime = TimeUtil.transformYearAndMonthAdnDay(admission.getEndTime().toEpochSecond(ZoneOffset.of("+8")));
            registrationTime = startTime + "-" + endTime;
        }


        Organization organization = organizationMapper.selectById(organizationId);
//        Organization organization = cacheService.getOrganizationMapperCache(organizationId);
        if (organization == null ) {
            return CommonResult.fail("没有该社团");
        }

        String avatarUrl;

        if (organization.getAvatarId() == null || organization.getAvatarId().isEmpty()) {
            avatarUrl = pictureUtil.getPictureURL(DefaultPictureIdEnum.ORG_AVATAR.getPictureId(), true);
        } else {
            avatarUrl = pictureUtil.getPictureURL(organization.getAvatarId(), false);
        }

//        Department department = cacheService.getDepartment(departmentId);
        Department department = departmentMapper.selectById(departmentId);
        if (department == null) {
            return CommonResult.fail("没有该部门");
        }


        String name = organization.getName() + "-" + department.getName();

        if (department.getDescription() != null) {
            departmentIntroduceDataList.add(new OrganizationOrDepartmentIntroduceData("部门介绍", department.getDescription()));
        }

        if (department.getStandard() != null) {
            departmentIntroduceDataList.add(new OrganizationOrDepartmentIntroduceData("纳新标准", department.getStandard()));
        }

        departmentIntroduceResult.setAvatarUrl(avatarUrl);
        departmentIntroduceResult.setName(name);
        departmentIntroduceResult.setRegistrationTime(registrationTime);
        departmentIntroduceResult.setDepartmentIntroduceDataList(departmentIntroduceDataList);
        return CommonResult.success(departmentIntroduceResult);
    }


}

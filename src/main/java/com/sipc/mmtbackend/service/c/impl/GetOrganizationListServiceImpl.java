package com.sipc.mmtbackend.service.c.impl;

import com.sipc.mmtbackend.mapper.AdmissionMapper;
import com.sipc.mmtbackend.mapper.OrganizationMapper;
import com.sipc.mmtbackend.mapper.OrganizationTagMergeMapper;
import com.sipc.mmtbackend.mapper.TagMapper;
import com.sipc.mmtbackend.pojo.c.param.organizationListData.OrganizationListData;
import com.sipc.mmtbackend.pojo.c.param.organizationListData.SearchOrganizationListData;
import com.sipc.mmtbackend.pojo.c.result.OrganizationListResult;
import com.sipc.mmtbackend.pojo.domain.Admission;
import com.sipc.mmtbackend.pojo.domain.Organization;
import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.service.c.CacheService;
import com.sipc.mmtbackend.service.c.GetOrganizationListService;
import com.sipc.mmtbackend.utils.PictureUtil.PictureUtil;
import com.sipc.mmtbackend.utils.PictureUtil.pojo.DefaultPictureIdEnum;
import com.sipc.mmtbackend.utils.time.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class GetOrganizationListServiceImpl implements GetOrganizationListService {

    private final AdmissionMapper admissionMapper;
    private final OrganizationMapper organizationMapper;
    private final OrganizationTagMergeMapper organizationTagMergeMapper;
    private final TagMapper tagMapper;

    private final PictureUtil pictureUtil;
    @Resource
    private CacheService cacheService;

    @Autowired
    public GetOrganizationListServiceImpl(AdmissionMapper admissionMapper, OrganizationMapper organizationMapper, OrganizationTagMergeMapper organizationTagMergeMapper, TagMapper tagMapper, PictureUtil pictureUtil) {
        this.admissionMapper = admissionMapper;
        this.organizationMapper = organizationMapper;
        this.organizationTagMergeMapper = organizationTagMergeMapper;
        this.tagMapper = tagMapper;
        this.pictureUtil = pictureUtil;
    }


    @Override
    public CommonResult<OrganizationListResult> getOrganizationListService() {

        OrganizationListResult organizationListMethod = getOrganizationListMethod();

        return CommonResult.success(organizationListMethod);

    }

    @Override
    public CommonResult<OrganizationListResult> searchOrganizationList(SearchOrganizationListData searchOrganizationListData) {

        //获取社团列表
        OrganizationListResult organizationListMethod = this.getOrganizationListMethod();
        List<OrganizationListData> organizationListDataList = organizationListMethod.getOrganizationListDataList();

        //关键词搜索
        Iterator<OrganizationListData> iterator = organizationListDataList.iterator();
        int i = 0;
        while (iterator.hasNext()) {
            OrganizationListData next = iterator.next();

            if (!next.getName().contains(searchOrganizationListData.getKeyWord())) {
                ++i;
                iterator.remove();
            }
        }
        organizationListMethod.setTotalNum(organizationListMethod.getTotalNum() - i);
        return CommonResult.success(organizationListMethod);

    }

    private OrganizationListResult getOrganizationListMethod() {
        OrganizationListResult organizationListResult = new OrganizationListResult();

        List<OrganizationListData> organizationListDataList = new ArrayList<>();

        //获取社团列表
        List<Organization> organizationList = organizationMapper.selectAll();

        //获取当前时间戳
        long nowTime = TimeUtil.getNowTime();
        int totalNum = 0;
        for (Organization organization : organizationList) {

            //获取头像链接和社团名称
            Admission admission = admissionMapper.selectByOrganizationIdOrderById(organization.getId());

            //获取tag标签
            List<Integer> tagIds = organizationTagMergeMapper.selectTagIdByOrganizationI(organization.getId());
            List<String> tags = tagMapper.selectNameByTagIds(tagIds);

            String avatarUrl;

            if (organization.getAvatarId() == null || organization.getAvatarId().isEmpty()) {
                avatarUrl = pictureUtil.getPictureURL(DefaultPictureIdEnum.ORG_AVATAR.getPictureId(), true);
            } else {
                avatarUrl = pictureUtil.getPictureURL(organization.getAvatarId(), false);
            }

            //TODO:更改图像链接
            avatarUrl = organization.getAvatarId();

            if (admission == null) {
                String registrationTime = "未开始";
                String tagStatus = "未开始";
                organizationListDataList.add(new OrganizationListData(0, organization.getId(), avatarUrl, organization.getName(), tags, registrationTime, tagStatus, organization.getDescription()));

                continue;
            }

            if (admission.getStartTime() == null || admission.getEndTime() == null) {
                String registrationTime = "未开始";
                String tagStatus = "未开始";
                organizationListDataList.add(new OrganizationListData(admission.getId(), organization.getId(), avatarUrl, organization.getName(), tags, registrationTime, tagStatus, organization.getDescription()));

                continue;
            }

            long sTime = admission.getStartTime().toEpochSecond(ZoneOffset.of("+8"));
            long eTime = admission.getEndTime().toEpochSecond(ZoneOffset.of("+8"));
            if (sTime > eTime) {
                long t = sTime;
                sTime = eTime;
                eTime = t;
            }

            //获取报名时间
            //一天的秒数 days
            long day = 86400;
            String startTime = TimeUtil.transformYearAndMonthAdnDay(sTime);
            String endTime = TimeUtil.transformYearAndMonthAdnDay(eTime - day);
            String registrationTime = startTime + "-" + endTime;


            String tagStatus;
            if (nowTime < sTime) {
                tagStatus = "未开始";
            } else if (nowTime > admission.getStartTime().toEpochSecond(ZoneOffset.of("+8")) && nowTime < admission.getEndTime().toEpochSecond(ZoneOffset.of("+8"))) {
                tagStatus = "报名中";
            } else {
                tagStatus = "已结束";
            }

            organizationListDataList.add(new OrganizationListData(admission.getId(), organization.getId(), avatarUrl, organization.getName(), tags, registrationTime, tagStatus, organization.getDescription()));

            ++totalNum;

        }
        organizationListResult.setOrganizationListDataList(organizationListDataList);
        organizationListResult.setTotalNum(totalNum);
        return organizationListResult;
    }
}

package com.sipc.mmtbackend.service.c.impl;

import com.sipc.mmtbackend.mapper.*;
import com.sipc.mmtbackend.pojo.c.result.DepartmentResult;
import com.sipc.mmtbackend.pojo.domain.Admission;

import com.sipc.mmtbackend.service.c.CacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class CacheServiceImpl implements CacheService {

    @Resource
    private AdmissionMapper admissionMapper;
//    @Resource
//    private AdmissionQuestionDataMapper questionDataMapper;
    @Resource
    private OrganizationDepartmentMergeMapper organizationDepartmentMergeMapper;
    @Resource
    private OrganizationTagMergeMapper organizationTagMergeMapper;
    @Resource
    private TagMapper tagMapper;
    @Resource
    private OrganizationRecruitMapper organizationRecruitMapper;
    @Resource
    private OrganizationMapper organizationMapper;
    @Resource
    private DepartmentMapper departmentMapper;
    @Resource
    private MessageTemplateMapper messageTemplateMapper;

//    @Override
//    @Cacheable(value = "roundCache", key = "#organizationId")
//    public Integer getCacheRound(Integer organizationId) {
//        System.out.println("读取数据库:id=" + organizationId);
//        return admissionMapper.selectRoundsByOrganizationId(organizationId);
//    }
//
//    @Override
//    @CacheEvict(value = "roundCache")
//    public void deleteRoundCache(Integer organizationId) {
//        System.out.println("删除缓存:id=" + organizationId);
//
//    }
//
//    @Override
////    @Cacheable(cacheNames = "list")
//    public AppraiseResult getList(Integer questionId, Integer userId) {
//        return new AppraiseResult(questionDataMapper.selectAll(questionId, userId));
//    }
//
//    @Override
////    @Cacheable(cacheNames = "message")
//    public MessageTemplate getTemplate(Integer organizationId, Integer type, Integer round) {
//        return messageTemplateMapper.selectByOrganizationIdAndType(organizationId, type,round);
//    }

    @Override
    @Cacheable(value = "AdmissionCache", key = "#admissionId")
    public Admission getAdmissionCache(Integer admissionId) {
        return admissionMapper.selectById(admissionId);
    }

    @Override
    @CacheEvict(value = "AdmissionCache")
    public void deleteAdmissionCache(Integer admissionId) {
    }

    @Override
    @Cacheable(value = "organizationDepartmentMergeCache", key = "#admissionId")
    public List<DepartmentResult> organizationDepartmentMergeCache(Integer admissionId) {

        Admission admission = admissionMapper.selectById(admissionId);
        if (admission == null) {
            return new ArrayList<>();
        } else {
            return departmentMapper.selectRelationByAdmissionId(admission.getOrganizationId());
        }

//        return organizationDepartmentMergeMapper.selectRelationByAdmissionId(admissionId);
    }

    @Override
    @CacheEvict(value = {"getOrganizationIntroduce", "getDepartmentAdmission", "getDepartmentIntroduce"}, allEntries = true)
    public void deleteOrganizationList() {
        log.info("社团及部门展示页：缓存清除");
    }

}

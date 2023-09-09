package com.sipc.mmtbackend.service.c;

import com.sipc.mmtbackend.pojo.c.result.DepartmentResult;
import com.sipc.mmtbackend.pojo.domain.Admission;
import com.sipc.mmtbackend.pojo.domain.MessageTemplate;


import java.util.List;

/**
 * @author meishuhao
 */
public interface CacheService {
//    Integer getCacheRound(Integer organizationId);
//
//    void deleteRoundCache(Integer organizationId);
//
//    AppraiseResult getList(Integer questionId, Integer userId);
//
//    MessageTemplate getTemplate(Integer organizationId, Integer type,Integer round);

    Admission getAdmissionCache(Integer admissionId);

    void deleteAdmissionCache(Integer admissionId);

    List<DepartmentResult> organizationDepartmentMergeCache(Integer admissionId);

    void deleteOrganizationList();
}

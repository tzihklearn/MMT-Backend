package com.sipc.mmtbackend.mapper;

import com.sipc.mmtbackend.pojo.domain.Admission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用于各类复杂的检查
 *
 * @author DoudiNCer
 */
@Mapper
public interface InterviewCheckMapper {

    /**
     * 查询组织活动的面试
     *
     * @param organizationId 组织ID
     * @return 正在进行的面试
     */
    Admission selectOrganizationActivateAdmission(
            @Param("organizationId") Integer organizationId
    );

    /**
     * 查询组织已开始的面试轮次
     *
     * @param admissionId 纳新ID
     * @return 该纳新下已开始的最大面试轮次
     */
    Integer selectOrganizationActivateInterviewRound(
            @Param("admissionId") Integer admissionId
    );
}

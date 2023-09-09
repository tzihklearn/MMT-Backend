package com.sipc.mmtbackend.mapper.c;

import com.sipc.mmtbackend.pojo.c.domain.UserDepartmentRegistration;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserDepartmentRegistrationMapper {

//    List<OrganizationOrderPo> selectOrganizationOderByAdmissionId(Integer admissionId);
//
//    List<Integer> selectUserIdByAdmissionIdAndOrganizationOrder(Integer admissionId,
//                                                                @Param("organizationOrders") List<Integer> organizationOrder);
//
//    List<Integer> selectUserIdByAdmissionIdAndDepartmentOrder(Integer admissionId,
//                                                              @Param("departmentOrders") List<Integer> departmentOrder);
//
//    List<DepartmentOrderPo> selectDepartmentOdersByAdmissionId(Integer admissionId);

//    List<Integer> selectUserIdsGroupByAdmissionIdAndOrganizationId1(Integer admissionId);
//
//    List<Integer> selectUserIdsByAdmissionIdAndDepartmentAndOrganizationOrderAndDepartmentOrder1(Integer admissionId,
//                                                                                                 Integer departmentId);

    List<Integer> selectUserIdsByAdmissionId(Integer admissionId);

    //获取志愿次序
//    OrderPo selectOrderByUserIdAndAdmissionIdAndDepartmentId(Integer userId, Integer admissionId, Integer departmentId);
//
//    //获取为特定志愿次序为该社团的人的userId
//    List<Integer> selectUserIdsByAdmissionIdAndDepartmentIdAndDepartmentOrder(Integer admissionId, Integer departmentId,
//                                                                              Integer departmentOrder);
//
//    List<Integer> selectUserIdsByAdmissionIdAndDepartmentId(Integer admissionId, Integer departmentId);
//
//    Integer selectDepartmentOrderByAdmissionIdAndUserIdAndDepartmentId(Integer admissionId, Integer userId,
//                                                                       Integer departmentId);
//
//    List<DepartmentOrderNumPo> selectDepartmentOrderPoByAdmissionIdAndDepartmentIdAndOrganizationOrder1(Integer admissionId, Integer departmentId);
//
//    List<Integer> selectUserIdsByAdmissionIdAndDepartmentIds(Integer admissionId,
//                                                             @Param("departmentIds") List<Integer> departmentId);

    Integer insertByExample(UserDepartmentRegistration userDepartmentRegistration);

    Integer deleteByUserIdAndAdmissionId(Integer userId, Integer admissionId);

    //获取总的志愿次序分组
//    List<DepartmentOrderGroupPo> selectDepartmentOrderGroupPoByUserIds(Integer admissionId, Integer departmentId,
//                                                                       @Param("userIds") List<Integer> userIds);
//
//    List<Integer> selectUserIdsByAdmissionIdAndDepartmentIdAndFirstDepartmentOrder(Integer admissionId,
//                                                                                   Integer departmentId,
//                                                                                   Integer departmentOrder);
}

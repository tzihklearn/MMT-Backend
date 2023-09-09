package com.sipc.mmtbackend.mapper.c;

import com.sipc.mmtbackend.pojo.c.domain.RegistrationFormJson;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RegistrationFromJsonMapper {

//    List<RegistrationFormJson> selectByAdmissionId(Integer admissionId);
//
//    List<RegistrationFormJson> selectByUserIdAndTime(@Param("userIds") List<Integer> userId, Long startTime,
//                                                     Long endTime);
//
//    List<RegistrationFormJson> selectByTime(Long startTime, Long endTime);
//
//    List<Integer> selectUserIdByAdmissionId(Integer admissionId);
//
//    List<RegistrationFormJson> selectByAdmissionIdAndUserIds(Integer admissionId,
//                                                             @Param("userIds") List<Integer> userId);

    Integer insert(RegistrationFormJson registrationFormJson);

    Integer updateByUserIdAndAdmissionId(RegistrationFormJson registrationFormJson);

    Integer selectByAdmissionIdAndUserId(Integer admissionId, Integer userId);

//    List<Integer> selectByAdmissionIdAndTime(Integer admissionId, Long startTime, Long endTime);

//    List<Integer> selectUserIdsByAdmissionIdAndOrder1AndTime(Integer admissionId, Integer departmentId, Long startTime, Long endTime);
//
//    List<Integer> selectUserIdsByAdmissionIdAndDepartmentOrderAndTime(Integer admissionId, Integer departmentId, Integer departmentOrder,
//                                                                      Long startTime, Long endTime);
//
//    List<Integer> selectUserIdsByAdmissionIdAndDepartmentIdAndOrdder1(Integer admissionId, Integer departmentId);

}

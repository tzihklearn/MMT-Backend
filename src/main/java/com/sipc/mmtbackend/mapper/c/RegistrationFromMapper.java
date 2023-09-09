package com.sipc.mmtbackend.mapper.c;

import com.sipc.mmtbackend.pojo.c.domain.RegistrationForm;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RegistrationFromMapper {

    List<Integer> selectIdByAdmissionIdAndQuestion(Integer admissionId, String question);

    List<RegistrationForm> selectIdByAdmissionId(Integer admissionId);

    int insert(RegistrationForm record);

    RegistrationForm selectByPrimaryKey(Integer id);

    int updateByPrimaryKey(RegistrationForm record);

    int deleteByPrimaryKey(Integer id);

    List<RegistrationForm> selectByAdmissionId(Integer admissionId);

    Integer deleteByAdmissionId(Integer admissionId);
}

package com.sipc.mmtbackend.mapper.c;


import com.sipc.mmtbackend.pojo.c.domain.DefaultQuestionState;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DefaultQuestionStateMapper {

    int insert(DefaultQuestionState record);

    DefaultQuestionState selectByPrimaryKey(Integer id);

    int updateByPrimaryKey(DefaultQuestionState record);

    int deleteByPrimaryKey(Integer id);

    List<DefaultQuestionState> selectByAdmissionId(Integer admissionId);

    DefaultQuestionState selectByAdmissionIdOrderById(Integer admissionId);
}

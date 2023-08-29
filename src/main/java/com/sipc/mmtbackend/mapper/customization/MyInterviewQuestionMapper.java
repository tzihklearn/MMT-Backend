package com.sipc.mmtbackend.mapper.customization;

import com.sipc.mmtbackend.mapper.InterviewQuestionMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MyInterviewQuestionMapper extends InterviewQuestionMapper {

    List<Integer> selectQuestionIdAndAdmissionIdAndRound(Integer admissionId, Integer round);

}

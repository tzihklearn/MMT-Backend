package com.sipc.mmtbackend.mapper.customization;

import com.sipc.mmtbackend.mapper.QuestionScoreMapper;
import com.sipc.mmtbackend.pojo.domain.InterviewQuestion;
import com.sipc.mmtbackend.pojo.domain.po.interviewReview.QuestionScorePo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MyQuestionScoreMapper extends QuestionScoreMapper {

    List<QuestionScorePo> selectPoAllByQuestionId(List<InterviewQuestion> questionIds);

}

package com.sipc.mmtbackend.mapper.customization;

import com.sipc.mmtbackend.mapper.QuestionScoreMapper;
import com.sipc.mmtbackend.pojo.domain.InterviewQuestion;
import com.sipc.mmtbackend.pojo.domain.QuestionScore;
import com.sipc.mmtbackend.pojo.domain.po.interviewReview.QuestionScorePo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MyQuestionScoreMapper extends QuestionScoreMapper {

    List<QuestionScorePo> selectPoAllByQuestionId(List<InterviewQuestion> questionIds);

    /**
     * 批量插入
     *
     * @param questionScores 要插入的数据
     * @return 受影响行数
     */
    int insertBatch(List<QuestionScore> questionScores);

}

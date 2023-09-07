package com.sipc.mmtbackend.mapper.customization;

import com.sipc.mmtbackend.mapper.InterviewStatusMapper;
import com.sipc.mmtbackend.pojo.domain.InterviewStatus;
import com.sipc.mmtbackend.pojo.domain.po.GroupIntCountPo;
import com.sipc.mmtbackend.pojo.domain.po.GroupLocalTimeCountPo;
import com.sipc.mmtbackend.pojo.domain.po.InterviewMessagePo;
import com.sipc.mmtbackend.pojo.domain.po.MyInterviewStatusPo;
import com.sipc.mmtbackend.pojo.domain.po.interviewReview.GroupByNumPo;
import com.sipc.mmtbackend.pojo.domain.po.interviewReview.IRInterviewStatusPo;
import com.sipc.mmtbackend.pojo.dto.param.dataDashboard.SiftParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author tzih
 * @version v1.0
 * @since 2023.08.14
 */
@Mapper
public interface MyInterviewStatusMapper extends InterviewStatusMapper {

    List<InterviewStatus> selectByAdmissionId(Integer admissionId);

//    List<InterviewStatus> selectByAdmissionIdAndLimit(Integer admissionId, Integer page, Integer pageNum);

    List<GroupIntCountPo> selectOrganizationOrderCountByAdmissionId(Integer admissionId);

    List<GroupIntCountPo> selectDepartmentOrderCountByAdmissionId(Integer admissionId);

    List<GroupIntCountPo> selectDepartmentCountByAdmissionId(Integer admissionId);

    List<GroupLocalTimeCountPo> selectNextTimeCountByAdmissionId(Integer admissionId);

    List<GroupIntCountPo> selectAddressCountByAdmissionId(Integer admissionId);

    List<MyInterviewStatusPo> selectAllAndUserInfoByAdmissionIdLimit(Integer admissionId, Integer start, Integer end);

    List<MyInterviewStatusPo> selectByAdmissionIdAndUserInfo(Integer admissionId);

    List<MyInterviewStatusPo> selectByAdmissionIdAndSift(SiftParam siftParam, Integer admissionId);

    List<InterviewMessagePo> selectIMByAdmissionIdAndRoundFirst(Integer admissionId, Integer start, Integer end);

    List<InterviewMessagePo> selectIMByAdmissionIdAndRound(Integer admissionId, Integer round, Integer start, Integer end);

    List<GroupIntCountPo> selectDepartmentCountByAdmissionIdAndRound(Integer admissionId, Integer round);

    List<GroupIntCountPo> selectAddressCountByAdmissionIdAndRound(Integer admissionId, Integer round);

    List<GroupIntCountPo> selectMessageStateByAdmissionIdAndRound(Integer admissionId, Integer round);

    List<InterviewMessagePo> selectArrangeByAdmissionIdAndSiftAndRoundFirst(com.sipc.mmtbackend.pojo.dto.param.interviewArrangement.SiftParam siftParam, Integer admissionId);

    List<InterviewMessagePo> selectArrangeByAdmissionIdAndSiftAndRound(com.sipc.mmtbackend.pojo.dto.param.interviewArrangement.SiftParam siftParam, Integer admissionId, Integer round);

    List<IRInterviewStatusPo> selectIRByAdmissionIdAndRoundAndNotTrueTime(Integer admissionId, Integer round);

    List<IRInterviewStatusPo> selectIRByAdmissionIdAndRoundAndNotTrueTimeLimit(Integer admissionId, Integer round, Integer start, Integer end);

    List<IRInterviewStatusPo> selectIRByAdmissionIdAndRoundAndNotTrueTimeSift(Integer admissionId, Integer round, com.sipc.mmtbackend.pojo.dto.param.interviewreview.SiftParam siftParam);

    List<GroupByNumPo> selectGroupByAdmissionIdAndRoundAndDAndA(Integer admissionId, Integer round);

    List<GroupByNumPo> selectGroupDByAdmissionIdAndRoundAndDAndA(Integer admissionId, Integer round);

    List<GroupByNumPo> selectGroupAByAdmissionIdAndRoundAndDAndA(Integer admissionId, Integer round);

    List<GroupByNumPo> selectGroupMessageByAdmissionIdAndRound(Integer admissionId, Integer round, Integer state);

    List<GroupByNumPo> selectGroupMessageByAdmissionIdAndRoundNoState(Integer admissionId, Integer round);
}

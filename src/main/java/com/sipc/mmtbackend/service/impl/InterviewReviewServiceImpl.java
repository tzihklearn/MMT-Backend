package com.sipc.mmtbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sipc.mmtbackend.common.Common;
import com.sipc.mmtbackend.mapper.*;
import com.sipc.mmtbackend.mapper.customization.MyInterviewQuestionMapper;
import com.sipc.mmtbackend.mapper.customization.MyInterviewStatusMapper;
import com.sipc.mmtbackend.mapper.customization.MyMajorClassMapper;
import com.sipc.mmtbackend.mapper.customization.MyQuestionScoreMapper;
import com.sipc.mmtbackend.pojo.domain.*;
import com.sipc.mmtbackend.pojo.domain.po.MajorClassPo;
import com.sipc.mmtbackend.pojo.domain.po.interviewReview.GroupByNumPo;
import com.sipc.mmtbackend.pojo.domain.po.interviewReview.IRInterviewStatusPo;
import com.sipc.mmtbackend.pojo.domain.po.interviewReview.QuestionScorePo;
import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.pojo.dto.param.interviewArrangement.po.MessageSendPo;
import com.sipc.mmtbackend.pojo.dto.param.interviewreview.ArrangeParam;
import com.sipc.mmtbackend.pojo.dto.param.interviewreview.SendParam;
import com.sipc.mmtbackend.pojo.dto.param.interviewreview.SiftParam;
import com.sipc.mmtbackend.pojo.dto.result.interviewArrangement.po.ResultOverviewPo;
import com.sipc.mmtbackend.pojo.dto.result.interviewreview.AddressResult;
import com.sipc.mmtbackend.pojo.dto.result.interviewreview.InfoAllResult;
import com.sipc.mmtbackend.pojo.dto.result.interviewreview.MessageTemplateResult;
import com.sipc.mmtbackend.pojo.dto.result.interviewreview.PieChatResult;
import com.sipc.mmtbackend.pojo.dto.result.interviewreview.po.*;
import com.sipc.mmtbackend.pojo.exceptions.DateBaseException;
import com.sipc.mmtbackend.service.InterviewReviewService;
import com.sipc.mmtbackend.utils.CheckroleBUtil.pojo.BTokenSwapPo;
import com.sipc.mmtbackend.utils.RedisUtil;
import com.sipc.mmtbackend.utils.ThreadLocalContextUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author tzih
 * @version v1.0
 * @since 2023.08.28
 */
@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Slf4j
public class InterviewReviewServiceImpl implements InterviewReviewService {

    private final AdmissionMapper admissionMapper;

    private final AdmissionDepartmentMergeMapper admissionDepartmentMergeMapper;

    private final AdmissionScheduleMapper admissionScheduleMapper;

    private final AdmissionAddressMapper admissionAddressMapper;

    private final InterviewStatusMapper interviewStatusMapper;

    private final MyInterviewStatusMapper myInterviewStatusMapper;

    private final MyMajorClassMapper myMajorClassMapper;

    private final InterviewQuestionMapper interviewQuestionMapper;

    private final MyInterviewQuestionMapper myInterviewQuestionMapper;

    private final QuestionScoreMapper questionScoreMapper;

    private final MyQuestionScoreMapper myQuestionScoreMapper;

    private final QuestionDataMapper questionDataMapper;

    private final DepartmentMapper departmentMapper;

    private final MessageTemplateMapper messageTemplateMapper;

    private final MessageMapper messageMapper;

    private final RedisUtil redisUtil;

    private final Map<Integer, String> departmentMap = new HashMap<>();

    private final Map<Integer, String> addressMap = new HashMap<>();

    private final Map<Integer, String> classMap = new HashMap<>();

    @Override
    public CommonResult<AddressResult> getAddress() {

        /*
          鉴权并且获取用户所属社团组织id
         */
        BTokenSwapPo context = ThreadLocalContextUtil.getContext();
        Integer organizationId = context.getOrganizationId();

        Admission admission = admissionMapper.selectOne(
                new QueryWrapper<Admission>()
                        .select("id")
                        .eq("organization_id", organizationId)
                        .orderByDesc("id")
                        .last("limit 1")
        );
        if (admission == null) {
            return CommonResult.fail("社团没有开始纳新");
        }

        int admissionId = admission.getId();

        List<AddressPo> addressPoList = new ArrayList<>();

        for (AdmissionDepartmentMerge admissionDepartmentMerge : admissionDepartmentMergeMapper.selectList(
                new QueryWrapper<AdmissionDepartmentMerge>().eq("admission_id", admissionId))
        ) {
            AdmissionSchedule admissionSchedule = admissionScheduleMapper.selectOne(
                    new QueryWrapper<AdmissionSchedule>()
                            .eq("admission_department_id", admissionDepartmentMerge.getId())
                            .orderByDesc("round")
                            .last("limit 1")
            );

            for (AdmissionAddress admissionAddress : admissionAddressMapper.selectList(
                    new QueryWrapper<AdmissionAddress>()
                            .eq("admission_schedule_id", admissionSchedule.getId()))
            ) {
                AddressPo addressPo = new AddressPo();
                addressPo.setId(admissionAddress.getId());
                addressPo.setName(admissionAddress.getName());
                addressPo.setDepartmentId(admissionDepartmentMerge.getDepartmentId());
                addressPo.setRound(admissionSchedule.getRound());

                addressPoList.add(addressPo);
            }

        }

        AddressResult result = new AddressResult();
        result.setAddressPoList(addressPoList);


        return CommonResult.success(result);
    }

    @Override
    public CommonResult<InfoAllResult> all(Integer page) {

        /*
          鉴权并且获取用户所属社团组织id
         */
        BTokenSwapPo context = ThreadLocalContextUtil.getContext();
        Integer organizationId = context.getOrganizationId();

        Admission admission = admissionMapper.selectOne(
                new QueryWrapper<Admission>()
                        .select("id")
                        .eq("organization_id", organizationId)
                        .orderByDesc("id")
                        .last("limit 1")
        );
        if (admission == null) {
            return CommonResult.fail("社团没有开始纳新");
        }

        if (classMap.isEmpty()) {
            for (MajorClassPo majorClassPo : myMajorClassMapper.selectAllAndAcaMajor()) {
                classMap.put(majorClassPo.getId(), majorClassPo.getName());
            }

        }

        int admissionId = admission.getId();

        List<QuestionPo> title = new ArrayList<>();

        List<InfoPo> tableData = new ArrayList<>();

        AdmissionDepartmentMerge admissionDepartmentMerge = admissionDepartmentMergeMapper.selectOne(
                new QueryWrapper<AdmissionDepartmentMerge>()
                        .eq("admission_id", admissionId)
                        .last("limit 1")
        );

        AdmissionSchedule admissionSchedule = admissionScheduleMapper.selectOne(
                new QueryWrapper<AdmissionSchedule>()
                        .eq("admission_department_id", admissionDepartmentMerge.getId())
                        .orderByDesc("round")
                        .last("limit 1")
        );

        //查找面试问题
        List<InterviewQuestion> interviewQuestions = interviewQuestionMapper.selectList(
                new QueryWrapper<InterviewQuestion>()
//                        .select("id")
//                        .select("question_id")
                        .eq("admission_id", admissionId)
                        .eq("round", admissionSchedule.getRound())
                        .eq("type", 6)
        );

        int i = 1;

        QuestionPo questionPoL = new QuestionPo();
        questionPoL.setLabel(0);
        questionPoL.setQuestion("面试总分");
        title.add(questionPoL);

        for (InterviewQuestion interviewQuestion : interviewQuestions) {
            QuestionData questionData = questionDataMapper.selectById(interviewQuestion.getQuestionId());

            QuestionPo questionPo = new QuestionPo();
            questionPo.setLabel(i);
            questionPo.setQuestion(questionData.getQuestion());

            title.add(questionPo);
            ++i;
        }

//        StateAllPo redisPo = redisUtil.getString("stateAll,admissionId:" + admissionId, StateAllPo.class);

//        List<Integer> questionIdList = myInterviewQuestionMapper.selectQuestionIdAndAdmissionIdAndRound(admissionId, admissionSchedule.getRound());

        Map<Integer, Double> questionScoreMap = new HashMap<>();

        for (QuestionScorePo questionScorePo : myQuestionScoreMapper.selectPoAllByQuestionId(interviewQuestions)) {
            BigDecimal b = BigDecimal.valueOf(questionScorePo.getScore());
            questionScoreMap.put(questionScorePo.getUserId() * Common.QuestionScoreSilt + questionScorePo.getInterviewQuestionId(), b.setScale(2, RoundingMode.HALF_UP).doubleValue());
        }

        int start = (page - 1) * 10;
        int end = page * 10;


        for (IRInterviewStatusPo irInterviewStatusPo :
                myInterviewStatusMapper.selectIRByAdmissionIdAndRoundAndNotTrueTimeLimit(
                        admissionId, admissionSchedule.getRound(), start, end
                )
        ) {
            InfoPo infoPo = new InfoPo();
            infoPo.setId(irInterviewStatusPo.getId());
            infoPo.setUserId(irInterviewStatusPo.getUserId());
            infoPo.setStudentId(irInterviewStatusPo.getStudentId());
            infoPo.setName(irInterviewStatusPo.getName());
            infoPo.setClassName(classMap.get(irInterviewStatusPo.getMajorClassId()));

            List<ScorePo> score = new ArrayList<>();

            ScorePo scoreAllPo = new ScorePo();
            scoreAllPo.setLabel(0);
            score.add(scoreAllPo);

            int j = 1;
            double sum = 0;
            for (InterviewQuestion interviewQuestion : interviewQuestions) {

                ScorePo scorePo = new ScorePo();
                scorePo.setLabel(j);

                double scoreNumber = questionScoreMap.get(irInterviewStatusPo.getUserId() * Common.QuestionScoreSilt + interviewQuestion.getId());
                scorePo.setScore(scoreNumber);

                sum += scoreNumber;

                score.add(scorePo);

                ++j;
            }

            scoreAllPo.setScore(sum);

            infoPo.setScore(score);
            int stateId = irInterviewStatusPo.getState();
            infoPo.setStateId(stateId);
            if (stateId == 9) {
                infoPo.setState("通过");
            } else if (stateId == 8) {
                infoPo.setState("失败");
            } else if (stateId == 7) {
                infoPo.setState("待定");
            } else {
                infoPo.setState("--");
            }

            int messageStateId = irInterviewStatusPo.getIsMessage();
            infoPo.setMessageStateId(messageStateId);
            if (messageStateId == 0) {
                infoPo.setMessageState("未安排");
            } else if (messageStateId == 1) {
                infoPo.setMessageState("已安排未通知");
            } else {
                infoPo.setMessageState("已通知");
            }

            tableData.add(infoPo);
        }

        InfoAllResult result = new InfoAllResult();
        result.setTitle(title);
        result.setTableData(tableData);

        int count = interviewStatusMapper.selectCount(
                new QueryWrapper<InterviewStatus>()
                        .eq("admission_id", admissionId)
                        .eq("round", admissionSchedule.getRound())
                        .isNotNull("true_end_time")
        ).intValue();
        result.setAllNum(count);
        result.setPage(page);
        if (count % 10 != 0) {
            result.setPageAll( count / 10 + 1);
        } else {
            result.setPageAll(count / 10);
        }

        result.setRound(admissionSchedule.getRound());

        return CommonResult.success(result);
    }

    @Override
    public CommonResult<InfoAllResult> sift(SiftParam siftParam) {

        /*
          鉴权并且获取用户所属社团组织id
         */
        BTokenSwapPo context = ThreadLocalContextUtil.getContext();
        Integer organizationId = context.getOrganizationId();

        Admission admission = admissionMapper.selectOne(
                new QueryWrapper<Admission>()
                        .select("id")
                        .eq("organization_id", organizationId)
                        .orderByDesc("id")
                        .last("limit 1")
        );
        if (admission == null) {
            return CommonResult.fail("社团没有开始纳新");
        }

        if (classMap.isEmpty()) {
            for (MajorClassPo majorClassPo : myMajorClassMapper.selectAllAndAcaMajor()) {
                classMap.put(majorClassPo.getId(), majorClassPo.getName());
            }

        }

        int admissionId = admission.getId();

        List<QuestionPo> title = new ArrayList<>();

        List<InfoPo> tableData = new ArrayList<>();

        AdmissionDepartmentMerge admissionDepartmentMerge = admissionDepartmentMergeMapper.selectOne(
                new QueryWrapper<AdmissionDepartmentMerge>()
                        .eq("admission_id", admissionId)
                        .last("limit 1")
        );

        AdmissionSchedule admissionSchedule = admissionScheduleMapper.selectOne(
                new QueryWrapper<AdmissionSchedule>()
                        .eq("admission_department_id", admissionDepartmentMerge.getId())
                        .orderByDesc("round")
                        .last("limit 1")
        );

        //查找面试问题
        List<InterviewQuestion> interviewQuestions = interviewQuestionMapper.selectList(
                new QueryWrapper<InterviewQuestion>()
                        .select("id")
                        .select("question_id")
                        .eq("admission_id", admissionId)
                        .eq("round", admissionSchedule.getRound())
                        .eq("type", 6)
        );

        int i = 1;

        QuestionPo questionPoL = new QuestionPo();
        questionPoL.setLabel(0);
        questionPoL.setQuestion("面试总分");
        title.add(questionPoL);

        for (InterviewQuestion interviewQuestion : interviewQuestions) {
            QuestionData questionData = questionDataMapper.selectById(interviewQuestion.getQuestionId());

            QuestionPo questionPo = new QuestionPo();
            questionPo.setLabel(i);
            questionPo.setQuestion(questionData.getQuestion());

            title.add(questionPo);
            ++i;
        }

//        StateAllPo redisPo = redisUtil.getString("stateAll,admissionId:" + admissionId, StateAllPo.class);

//        List<Integer> questionIdList = myInterviewQuestionMapper.selectQuestionIdAndAdmissionIdAndRound(admissionId, admissionSchedule.getRound());

        Map<Integer, Double> questionScoreMap = new HashMap<>();

        for (QuestionScorePo questionScorePo : myQuestionScoreMapper.selectPoAllByQuestionId(interviewQuestions)) {
            BigDecimal b = BigDecimal.valueOf(questionScorePo.getScore());
            questionScoreMap.put(questionScorePo.getUserId() * Common.QuestionScoreSilt + questionScorePo.getInterviewQuestionId(), b.setScale(2, RoundingMode.HALF_UP).doubleValue());
        }


        if (siftParam.getStateList() != null) {
            List<Integer> po = new ArrayList<>();
            for (Integer integer : siftParam.getStateList()) {
                if (integer == 1) {
                    for (int j = 0; j < 7; ++j) {
                        po.add(j);
                    }
                } else if (integer == 2) {
                    po.add(7);
                } else if (integer == 3) {
                    po.add(9);
                } else if (integer == 4) {
                    po.add(8);
                } else {
                    return CommonResult.fail("面试结果筛选参数不正确");
                }
            }
            siftParam.setStateList(po);
        }


        List<IRInterviewStatusPo> irInterviewStatusPos = myInterviewStatusMapper.selectIRByAdmissionIdAndRoundAndNotTrueTimeSift(
                admissionId, admissionSchedule.getRound(), siftParam
        );

        int start = (siftParam.getPage() - 1) * 10;
        int end = siftParam.getPage() * 10;
        int q = 0;
        for (IRInterviewStatusPo irInterviewStatusPo : irInterviewStatusPos) {
            if (q >= start && q < end) {
                InfoPo infoPo = new InfoPo();
                infoPo.setId(irInterviewStatusPo.getId());
                infoPo.setUserId(irInterviewStatusPo.getUserId());
                infoPo.setStudentId(irInterviewStatusPo.getStudentId());
                infoPo.setName(irInterviewStatusPo.getName());
                infoPo.setClassName(classMap.get(irInterviewStatusPo.getMajorClassId()));

                List<ScorePo> score = new ArrayList<>();

                ScorePo scoreAllPo = new ScorePo();
                scoreAllPo.setLabel(0);
                score.add(scoreAllPo);

                int j = 1;
                double sum = 0;
                for (InterviewQuestion interviewQuestion : interviewQuestions) {

                    ScorePo scorePo = new ScorePo();
                    scorePo.setLabel(j);
                    double scoreNumber = questionScoreMap.get(irInterviewStatusPo.getUserId() * Common.QuestionScoreSilt + interviewQuestion.getId());
                    scorePo.setScore(scoreNumber);

                    sum += scoreNumber;

                    score.add(scorePo);

                    ++j;
                }

                scoreAllPo.setScore(sum);

                infoPo.setScore(score);
                int stateId = irInterviewStatusPo.getState();

                if (stateId == 9) {
                    infoPo.setStateId(3);
                    infoPo.setState("通过");
                } else if (stateId == 8) {
                    infoPo.setStateId(4);
                    infoPo.setState("失败");
                } else if (stateId == 7) {
                    infoPo.setStateId(2);
                    infoPo.setState("待定");
                } else {
                    infoPo.setStateId(1);
                    infoPo.setState("--");
                }

                int messageStateId = irInterviewStatusPo.getIsMessage();
                infoPo.setMessageStateId(messageStateId);
                if (messageStateId == 0) {
                    infoPo.setMessageState("未安排");
                } else if (messageStateId == 1) {
                    infoPo.setMessageState("已安排未通知");
                } else {
                    infoPo.setMessageState("已通知");
                }

                tableData.add(infoPo);
            }
            if (q >= end) {
                break;
            }
        }

        InfoAllResult result = new InfoAllResult();
        result.setTitle(title);
        result.setTableData(tableData);
        int count = interviewQuestions.size();
        result.setAllNum(count);
        result.setPage(siftParam.getPage());
        if (count % 10 != 0) {
            result.setPageAll( count / 10 + 1);
        } else {
            result.setPageAll(count / 10);
        }

        result.setRound(admissionSchedule.getRound());

        return CommonResult.success(result);
    }

    @Override
    public CommonResult<PieChatResult> pieChatInfo(Integer departmentId, Integer addressId) {

        /*
          鉴权并且获取用户所属社团组织id
         */
        BTokenSwapPo context = ThreadLocalContextUtil.getContext();
        Integer organizationId = context.getOrganizationId();

        Admission admission = admissionMapper.selectOne(
                new QueryWrapper<Admission>()
                        .select("id")
                        .eq("organization_id", organizationId)
                        .orderByDesc("id")
                        .last("limit 1")
        );
        if (admission == null) {
            return CommonResult.fail("社团没有开始纳新");
        }

        if (departmentMap.isEmpty()) {
            for (Department department : departmentMapper.selectList(new QueryWrapper<>())) {
                departmentMap.putIfAbsent(department.getId(), department.getName());
            }
        }

        if (addressMap.isEmpty()) {
            for (AdmissionAddress admissionAddress : admissionAddressMapper.selectList(new QueryWrapper<>())) {
                addressMap.putIfAbsent(admissionAddress.getId(), admissionAddress.getName());
            }
        }

        int admissionId = admission.getId();

        AdmissionDepartmentMerge admissionDepartmentMerge = admissionDepartmentMergeMapper.selectOne(
                new QueryWrapper<AdmissionDepartmentMerge>()
                        .eq("admission_id", admissionId)
                        .last("limit 1")
        );

        if (admissionDepartmentMerge == null) {
            return CommonResult.fail("该社团没有部门参与纳新");
        }

        AdmissionSchedule admissionSchedule = admissionScheduleMapper.selectOne(
                new QueryWrapper<AdmissionSchedule>()
                        .eq("admission_department_id", admissionDepartmentMerge.getId())
                        .orderByDesc("round")
                        .last("limit 1")
        );

        if (admissionSchedule == null) {
            return CommonResult.fail("改社团没有发起任何一轮面试");
        }

        PieChatAllPo pieChatAll = new PieChatAllPo();

        ResultOverviewPo resultOverview = new ResultOverviewPo();

        List<PieChartPo> departmentDivide = new ArrayList<>();

        List<PieChartPo> addressDivide = new ArrayList<>();

        PieChartPo pieChartPoL = new PieChartPo();
        pieChartPoL.setId(1);
        pieChartPoL.setContent("未操作");
        pieChartPoL.setNum(0);
        resultOverview.setNotOperated(pieChartPoL);

        pieChartPoL = new PieChartPo();
        pieChartPoL.setId(2);
        pieChartPoL.setContent("失败");
        pieChartPoL.setNum(0);
        resultOverview.setFail(pieChartPoL);

        pieChartPoL = new PieChartPo();
        pieChartPoL.setId(3);
        pieChartPoL.setContent("成功");
        pieChartPoL.setNum(0);
        resultOverview.setPass(pieChartPoL);

        pieChartPoL = new PieChartPo();
        pieChartPoL.setId(4);
        pieChartPoL.setContent("待定");
        pieChartPoL.setNum(0);
        resultOverview.setUndetermined(pieChartPoL);


        for (GroupByNumPo groupByNumPo : myInterviewStatusMapper.selectGroupByAdmissionIdAndRoundAndDAndA(admissionId, admissionSchedule.getRound(), departmentId, addressId)) {
            Integer state = groupByNumPo.getId();
            if (state == 7) {
                PieChartPo pieChartPo = new PieChartPo();
                pieChartPo.setId(4);
                pieChartPo.setContent("待定");
                pieChartPo.setNum(groupByNumPo.getNum());
                resultOverview.setUndetermined(pieChartPo);
            } else if (state == 8) {
                PieChartPo pieChartPo = new PieChartPo();
                pieChartPo.setId(2);
                pieChartPo.setContent("失败");
                pieChartPo.setNum(groupByNumPo.getNum());

                resultOverview.setFail(pieChartPo);
            } else if (state == 9) {
                PieChartPo pieChartPo = new PieChartPo();
                pieChartPo.setId(3);
                pieChartPo.setContent("成功");
                pieChartPo.setNum(groupByNumPo.getNum());

                resultOverview.setPass(pieChartPo);
            } else {
                pieChartPoL.setNum(pieChartPoL.getNum() + groupByNumPo.getNum());
            }
        }

//        List<Integer> notDepartmentId = new ArrayList<>();

        for (GroupByNumPo groupByNumPo : myInterviewStatusMapper.selectGroupDByAdmissionIdAndRoundAndDAndA(admissionId, admissionSchedule.getRound(), departmentId, addressId)) {
            PieChartPo pieChartPo = new PieChartPo();
            pieChartPo.setId(groupByNumPo.getId());
            pieChartPo.setContent(departmentMap.get(groupByNumPo.getId()));
            pieChartPo.setNum(groupByNumPo.getNum());

            departmentDivide.add(pieChartPo);

//            notDepartmentId.add(groupByNumPo.getId());
        }

//        for (AdmissionDepartmentMerge departmentMerge : admissionDepartmentMergeMapper.selectList(
//                new QueryWrapper<AdmissionDepartmentMerge>()
//                        .eq("admission_id", admissionId)
//                        .notIn("department_id", notDepartmentId)
//        )) {
//            PieChartPo pieChartPo = new PieChartPo();
//            pieChartPo.setId(departmentMerge.getId());
//            pieChartPo.setContent(departmentMap.get(departmentMerge.getId()));
//            pieChartPo.setNum(0);
//
//            departmentDivide.add(pieChartPo);
//        }

//        List<Integer> notAddressId = new ArrayList<>();

        for (GroupByNumPo groupByNumPo : myInterviewStatusMapper.selectGroupAByAdmissionIdAndRoundAndDAndA(admissionId, admissionSchedule.getRound(), departmentId, addressId)) {
            PieChartPo pieChartPo = new PieChartPo();
            pieChartPo.setId(groupByNumPo.getId());
            pieChartPo.setContent(addressMap.get(groupByNumPo.getId()));
            pieChartPo.setNum(groupByNumPo.getNum());

            addressDivide.add(pieChartPo);
        }

//        admissionAddressMapper.selectList(
//                new QueryWrapper<AdmissionAddress>()
//        )

        pieChatAll.setResultOverview(resultOverview);
        pieChatAll.setDepartmentDivide(departmentDivide);
        pieChatAll.setAddressDivide(addressDivide);

        PieChatResult result = new PieChatResult();
        result.setPieChatAll(pieChatAll);
        return CommonResult.success(result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<String> arrange(ArrangeParam arrangeParam) throws DateBaseException {

        for (Integer id : arrangeParam.getInterviewIdList()) {
            InterviewStatus interviewStatus = new InterviewStatus();
            interviewStatus.setId(id);
            interviewStatus.setIsMessage(2);
            int updateNum = interviewStatusMapper.updateById(interviewStatus);
            if (updateNum != 1) {
                log.error("面试复盘安排通知接口异常，interview_status表更新数据数错误，受影响的行数：{}，要更新数据：{}", updateNum, interviewStatus);
                throw new DateBaseException("数据库更新操作异常");
            }
        }

        return CommonResult.success("操作成功");
    }

    @Override
    public CommonResult<MessageTemplateResult> messageTemplate(Byte status) {

        /*
          鉴权并且获取用户所属社团组织id
         */
        BTokenSwapPo context = ThreadLocalContextUtil.getContext();
        Integer organizationId = context.getOrganizationId();

        Admission admission = admissionMapper.selectOne(
                new QueryWrapper<Admission>()
                        .select("id")
                        .eq("organization_id", organizationId)
                        .orderByDesc("id")
                        .last("limit 1")
        );
        if (admission == null) {
            return CommonResult.fail("社团没有开始纳新");
        }

        int admissionId = admission.getId();

        AdmissionDepartmentMerge admissionDepartmentMerge = admissionDepartmentMergeMapper.selectOne(
                new QueryWrapper<AdmissionDepartmentMerge>()
                        .eq("admission_id", admissionId)
                        .last("limit 1")
        );

        AdmissionSchedule admissionSchedule = admissionScheduleMapper.selectOne(
                new QueryWrapper<AdmissionSchedule>()
                        .eq("admission_department_id", admissionDepartmentMerge.getId())
                        .orderByDesc("round")
                        .last("limit 1")
        );

        MessageTemplateResult result = new MessageTemplateResult();

        int sum = 0;

        int state;
        int type;
        if (status == 1) {
            state = 9;
            type = 2;
        } else if (status == 0) {
            state = 8;
            type = 3;
        } else {
            return CommonResult.fail("参数不正确");
        }
        for (GroupByNumPo groupByNumPo : myInterviewStatusMapper.selectGroupMessageByAdmissionIdAndRound(admissionId, admissionSchedule.getRound(), state)) {
            if (groupByNumPo.getId() == 2) {
                result.setNotNotifiedNum(groupByNumPo.getNum());
                sum += groupByNumPo.getNum();
            } else if (groupByNumPo.getId() == 3) {
                result.setNotifiedNum(groupByNumPo.getNum());
                sum += groupByNumPo.getNum();
            }
        }

        result.setAllNum(sum);

        MessageTemplate messageTemplate = messageTemplateMapper.selectOne(
                new QueryWrapper<MessageTemplate>()
                        .eq("organization_id", organizationId)
                        .eq("type", type)
        );

        if (messageTemplate == null) {
            return CommonResult.fail("未配置消息模板");
        }

        result.setMessageTemplate(messageTemplate.getMessageTemplate());

        return CommonResult.success(result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<String> send(SendParam sendParam) throws DateBaseException {

        /*
          鉴权并且获取用户所属社团组织id
         */
        BTokenSwapPo context = ThreadLocalContextUtil.getContext();
        Integer organizationId = context.getOrganizationId();

        for (MessageSendPo messageSendPo : sendParam.getMessageSendPoList()) {
            InterviewStatus interviewStatus = new InterviewStatus();
            interviewStatus.setId(messageSendPo.getInterviewId());
            interviewStatus.setIsMessage(3);
            int updateNum = interviewStatusMapper.updateById(interviewStatus);
            if (updateNum != 1) {
                log.error("面试复盘消息通知接口异常，interview_status表更新数据数错误，受影响的行数：{}，要更新数据：{}", updateNum, interviewStatus);
                throw new DateBaseException("数据库更新操作异常");
            }

            Message message = new Message();
            message.setMessage(sendParam.getMessage());
            message.setTime(LocalDateTime.now());
            message.setState(3);
            message.setIsRead((byte) 0);
            message.setOrganizationId(organizationId);
            message.setUserId(messageSendPo.getUserId());
            message.setType(1);
            message.setInterviewStatusId(messageSendPo.getInterviewId());
            message.setIsDeleted((byte) 0);
            int insertNum = messageMapper.insert(message);
            if (insertNum != 1) {
                log.error("面试复盘消息通知接口异常，message表新增数据数错误，受影响的行数：{}，要新增数据：{}", insertNum, message);
                throw new DateBaseException("数据库更新操作异常");
            }
        }

        return CommonResult.success("操作成功");
    }
}

package com.sipc.mmtbackend.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sipc.mmtbackend.mapper.*;
import com.sipc.mmtbackend.mapper.customization.MyQuestionScoreMapper;
import com.sipc.mmtbackend.pojo.domain.*;
import com.sipc.mmtbackend.pojo.domain.po.RealtimeInterviewPo.InterviewEvaluationAndAnswerPo;
import com.sipc.mmtbackend.pojo.domain.po.RealtimeInterviewPo.InterviewEvaluationQAPo;
import com.sipc.mmtbackend.pojo.domain.po.RealtimeInterviewPo.InterviewStatusPo;
import com.sipc.mmtbackend.pojo.domain.po.RealtimeInterviewPo.ProgressBarPo;
import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.pojo.dto.data.QuestionValueListData;
import com.sipc.mmtbackend.pojo.dto.param.RealtimeInterview.FinishInterviewParam;
import com.sipc.mmtbackend.pojo.dto.param.RealtimeInterview.PostInterviewCommentParam;
import com.sipc.mmtbackend.pojo.dto.param.RealtimeInterview.PutInterviewPlaceParam;
import com.sipc.mmtbackend.pojo.dto.param.RealtimeInterview.po.InterviewEvaluationPo;
import com.sipc.mmtbackend.pojo.dto.result.RealtimeIntreviewdResult.GetInterviewCommentResult;
import com.sipc.mmtbackend.pojo.dto.result.RealtimeIntreviewdResult.GetInterviewPlacesResult;
import com.sipc.mmtbackend.pojo.dto.result.RealtimeIntreviewdResult.GetInterviewProgressBarResult;
import com.sipc.mmtbackend.pojo.dto.result.RealtimeIntreviewdResult.GetIntervieweeListResult;
import com.sipc.mmtbackend.pojo.dto.result.RealtimeIntreviewdResult.po.*;
import com.sipc.mmtbackend.pojo.dto.result.po.KVPo;
import com.sipc.mmtbackend.service.RealtimeInterviewService;
import com.sipc.mmtbackend.utils.CheckinQRCodeUtil.CheckinQRCodeUtil;
import com.sipc.mmtbackend.utils.CheckroleBUtil.pojo.BTokenSwapPo;
import com.sipc.mmtbackend.utils.JsonUtil;
import com.sipc.mmtbackend.utils.ThreadLocalContextUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Slf4j
public class RealtimeInterviewServiceImpl implements RealtimeInterviewService {
    private final InterviewCheckMapper interviewCheckMapper;
    private final RealtimeInterviewMapper realtimeInterviewMapper;
    private final InterviewStatusMapper interviewStatusMapper;
    private final InterviewEvaluationMapper interviewEvaluationMapper;
    private final MyQuestionScoreMapper questionScoreMapper;
    private final CheckinQRCodeUtil checkinQRCodeUtil;
    private final JsonUtil jsonUtil;

    /**
     * 获取签到二维码
     *
     * @return 签到二维码的 Base64 编码
     */
    @Override
    public CommonResult<String> getCheckinQRCode() {
        BTokenSwapPo context = ThreadLocalContextUtil.getContext();
        Admission admission = interviewCheckMapper.selectOrganizationActivateAdmission(context.getOrganizationId());
        if (admission == null) {
            log.warn("用户 " + context + " 尝试在无活动的纳新时生成签到二维码");
            return CommonResult.fail("生成失败：未开始纳新或纳新已结束");
        }
        String qrcode = checkinQRCodeUtil.getCheckinQRCode(context.getOrganizationId(), context.getUserId());
        if (qrcode == null){
            log.warn("User: " + context + " Create Checkin QR Code Error\n");
            return CommonResult.serverError();
        }
        CommonResult<String> result = CommonResult.success();
        result.setData(qrcode);
        return result;
    }

    /**
     * 获取面试场地
     *
     * @return 所有面试场地
     */
    @Override
    public CommonResult<GetInterviewPlacesResult> getInterviewPlaces() {
        BTokenSwapPo context = ThreadLocalContextUtil.getContext();
        Admission admission = interviewCheckMapper.selectOrganizationActivateAdmission(context.getOrganizationId());
        if (admission == null) {
            log.warn("用户 " + context + " 尝试在无活动的纳新时获取面试场地");
            return CommonResult.fail("生成失败：未开始纳新或纳新已结束");
        }
        Integer maxRound = interviewCheckMapper.selectOrganizationActivateInterviewRound(admission.getId());
        if (maxRound == null){
            log.warn("用户 " + context + " 在纳新 " + admission + " 中未查询到任何面试");
            return CommonResult.fail("当前纳新未开启面试");
        }
        List<AdmissionAddress> addresses = interviewCheckMapper.selectAvailableInterviewAddress(maxRound, admission.getId(), 0);
        GetInterviewPlacesResult result = new GetInterviewPlacesResult();
        result.setCount(addresses.size());
        List<KVPo> results = new ArrayList<>();
        for (AdmissionAddress address : addresses) {
            results.add(new KVPo(address.getId(), address.getName()));
        }
        result.setPlaces(results);
        return CommonResult.success(result);
    }

    /**
     * 获取面试进度条
     *
     * @param placeId 面试场地 ID
     * @return 面试进度条数据
     */
    @Override
    public CommonResult<GetInterviewProgressBarResult> getInterviewProgressBar(int placeId) {
        BTokenSwapPo context = ThreadLocalContextUtil.getContext();
        Admission admission = interviewCheckMapper.selectOrganizationActivateAdmission(context.getOrganizationId());
        if (admission == null) {
            log.warn("用户 " + context + " 尝试在无活动的纳新时获取面试进度条");
            return CommonResult.fail("生成失败：未开始纳新或纳新已结束");
        }
        Integer maxRound = interviewCheckMapper.selectOrganizationActivateInterviewRound(admission.getId());
        if (maxRound == null){
            log.warn("用户 " + context + " 在纳新 " + admission + " 中未查询到任何面试");
            return CommonResult.fail("当前纳新未开启面试");
        }
        List<ProgressBarPo> progressBarPos = realtimeInterviewMapper.selectInterviewProgress(maxRound, admission.getId(), placeId);
        GetInterviewProgressBarResult result = new GetInterviewProgressBarResult();
        List<ProgressBarDataPo> results = new ArrayList<>();
        for (ProgressBarPo pbp : progressBarPos) {
            ProgressBarDataPo pbdp = new ProgressBarDataPo();
            pbdp.setFinished(pbp.getFinished());
            pbdp.setTime(pbp.getHour());
            pbdp.setTotal(pbp.getTotal());
            results.add(pbdp);
        }
        result.setBars(results);
        result.setGroupNum(results.size());
        return CommonResult.success(result);
    }

    /**
     * 结束面试
     *
     * @param param 面试ID
     * @return 处理结果
     */
    @Override
    public CommonResult<String> finishInterview(FinishInterviewParam param) {
        BTokenSwapPo context = ThreadLocalContextUtil.getContext();
        Admission admission = interviewCheckMapper.selectOrganizationActivateAdmission(context.getOrganizationId());
        if (admission == null) {
            log.warn("用户 " + context + " 尝试在无活动的纳新时获取面试进度条");
            return CommonResult.fail("生成失败：未开始纳新或纳新已结束");
        }
        Integer maxRound = interviewCheckMapper.selectOrganizationActivateInterviewRound(admission.getId());
        if (maxRound == null){
            log.warn("用户 " + context + " 在纳新 " + admission + " 中未查询到任何面试");
            return CommonResult.fail("当前纳新未开启面试");
        }
        InterviewStatus interviewStatus = interviewStatusMapper.selectById(param.getInterviewId());
        if (interviewStatus == null){
            log.warn("用户 " + context + " 尝试结束不存在的面试 " + param + "\n");
            return CommonResult.fail("面试不存在或不属于当前纳新");
        } else if (!Objects.equals(interviewStatus.getAdmissionId(), admission.getId())){
            log.warn("用户 " + context + " 尝试结束不属于当前纳新 " + admission + " 的面试 " + interviewStatus + "\n");
            return CommonResult.fail("面试不存在或不属于当前纳新");
        }
        if (interviewStatus.getState() != 6){
            log.warn("用户 " + context +" 尝试结束并非正在进行的面试 " + interviewStatus + "\n");
            if (interviewStatus.getState() == 7){
                return CommonResult.success("面试已结束，请勿重复请求");
            } else {
                return CommonResult.fail("面试未开始或已结束");
            }
        }
        interviewStatus.setState(7);
        interviewStatus.setTrueEndTime(LocalDateTime.now());
        int update = interviewStatusMapper.updateById(interviewStatus);
        if (update != 1){
            log.warn("结束面试失败: " + interviewStatus + " ,受影响的行数: " + update + "\n");
            return CommonResult.serverError();
        }
        return CommonResult.success();
    }

    /**
     * 获取面试人员名单
     *
     * @param pageId  第几页
     * @param keyword 搜索关键词
     * @param placeId 面试场地ID
     * @return 被面试这名单
     */
    @Override
    public CommonResult<GetIntervieweeListResult> getIntervieweeList(int pageId, String keyword, int placeId) {
        BTokenSwapPo context = ThreadLocalContextUtil.getContext();
        Admission admission = interviewCheckMapper.selectOrganizationActivateAdmission(context.getOrganizationId());
        if (admission == null) {
            log.warn("用户 " + context + " 尝试在无活动的纳新时获取面试进度条");
            return CommonResult.fail("生成失败：未开始纳新或纳新已结束");
        }
        Integer maxRound = interviewCheckMapper.selectOrganizationActivateInterviewRound(admission.getId());
        if (maxRound == null){
            log.warn("用户 " + context + " 在纳新 " + admission + " 中未查询到任何面试");
            return CommonResult.fail("当前纳新未开启面试");
        }
        Page<InterviewStatusPo> page = new Page<>(pageId, 10);
        IPage<InterviewStatusPo> iPage = realtimeInterviewMapper.selectRealtimeInterviewData(
                page, keyword, maxRound, admission.getId(), placeId);
        if (iPage.getPages() < pageId){
            page.setCurrent(iPage.getPages());
            iPage = realtimeInterviewMapper.selectRealtimeInterviewData(
                    page, keyword, maxRound, admission.getId(), placeId);
        }
        GetIntervieweeListResult result = new GetIntervieweeListResult();
        List<IntervieweePo> results = new ArrayList<>();
        for (InterviewStatusPo isp : iPage.getRecords()) {
            IntervieweePo ip = new IntervieweePo();
            ip.setId(isp.getId());
            ip.setCId(isp.getCId());
            ip.setStudentId(isp.getStudentId());
            ip.setName(isp.getName());
            ip.setClassName(isp.getClassName());
            ip.setDepartment(isp.getDepartment());
            ip.setTime(isp.getTime());
            ip.setPlace(isp.getPlace());
            // 0拒绝 1调整时间(待定) 2未安排 3已安排未通知 4已通知未签到 5已签到 6面试中(已面试，还没结果) 7待定 8失败 9通过
            ip.setSigned(isp.getState() >= 5);
            if (isp.getState() < 6){
                ip.setStatus(0);
            } else if (isp.getState() == 6){
                ip.setStatus(1);
            } else {
                ip.setStatus(2);
            }
            results.add(ip);
        }
        result.setInterviewees(results);
        result.setCount(results.size());
        result.setPages((int) iPage.getPages());
        return CommonResult.success(result);
    }

    /**
     * 修改面试场地
     *
     * @param param 场地ID
     * @return 处理结果
     */
    @Override
    public CommonResult<String> putInterviewPlace(PutInterviewPlaceParam param) {
        BTokenSwapPo context = ThreadLocalContextUtil.getContext();
        Admission admission = interviewCheckMapper.selectOrganizationActivateAdmission(context.getOrganizationId());
        if (admission == null) {
            log.warn("用户 " + context + " 尝试在无活动的纳新时修改面试场地");
            return CommonResult.fail("生成失败：未开始纳新或纳新已结束");
        }
        Integer maxRound = interviewCheckMapper.selectOrganizationActivateInterviewRound(admission.getId());
        if (maxRound == null){
            log.warn("用户 " + context + " 在纳新 " + admission + " 中未查询到任何面试");
            return CommonResult.fail("当前纳新未开启面试");
        }
        InterviewStatus interviewStatus = interviewStatusMapper.selectById(param.getInterviewId());
        if (interviewStatus == null){
            log.warn("用户 " + context + " 尝试修改不存在的面试 " + param + "\n");
            return CommonResult.fail("面试不存在或不属于当前纳新");
        } else if (!Objects.equals(interviewStatus.getAdmissionId(), admission.getId())){
            log.warn("用户 " + context + " 尝试修改不属于当前纳新 " + admission + " 的面试 " + interviewStatus + "\n");
            return CommonResult.fail("面试不存在或不属于当前纳新");
        }
        AdmissionAddress admissionAddress = interviewCheckMapper.selectAddressByIdAndNowData(param.getPlaceId(), maxRound, admission.getId(), interviewStatus.getDepartmentId());
        if (admissionAddress == null){
            log.warn("用户 " + context + " 尝试将面试 " + param + " 的场地修改为 " + param.getPlaceId() + "\n");
            return CommonResult.fail("面试场地不存在或不属于当前面试部门");
        }
        interviewStatus.setAdmissionAddressId(param.getPlaceId());
        int update = interviewStatusMapper.updateById(interviewStatus);
        if (update != 1){
            log.warn("修改面试场地失败: " + interviewStatus + " ,受影响的行数: " + update + "\n");
            return CommonResult.serverError();
        }
        return CommonResult.success();
    }

    /**
     * 获取本轮与已结束轮次面试评价问题与回答
     *
     * @param interviewId 本论面试ID
     * @return 面试评价
     */
    @Override
    public CommonResult<GetInterviewCommentResult> getInterviewComment(Integer interviewId) {
        BTokenSwapPo context = ThreadLocalContextUtil.getContext();
        Admission admission = interviewCheckMapper.selectOrganizationActivateAdmission(context.getOrganizationId());
        if (admission == null) {
            log.warn("用户 " + context + " 尝试在无活动的纳新时修改面试场地");
            return CommonResult.fail("生成失败：未开始纳新或纳新已结束");
        }
        Integer maxRound = interviewCheckMapper.selectOrganizationActivateInterviewRound(admission.getId());
        if (maxRound == null){
            log.warn("用户 " + context + " 在纳新 " + admission + " 中未查询到任何面试");
            return CommonResult.fail("当前纳新未开启面试");
        }
        InterviewStatus interviewStatus = interviewStatusMapper.selectById(interviewId);
        if (interviewStatus == null){
            log.warn("用户 " + context + " 尝试查询不存在的面试 " + interviewId + "的评价信息\n");
            return CommonResult.fail("面试不存在或不属于当前纳新");
        } else if (!Objects.equals(interviewStatus.getAdmissionId(), admission.getId())){
            log.warn("用户 " + context + " 尝试查询不属于当前纳新 " + admission + " 的面试 " + interviewStatus + "的评价信息\n");
            return CommonResult.fail("面试不存在或不属于当前纳新");
        }
        if (interviewStatus.getState() < 3) {
            log.warn("用户 " + context + " 尝试查询未安排的面试 " + interviewStatus + " 的评价信息");
            return CommonResult.fail("面试未安排");
        }
        if (interviewStatus.getState() > 6) {
            log.warn("用户 " + context + " 尝试查询已结束的面试 " + interviewStatus + " 的评价信息");
            return CommonResult.fail("面试已结束");
        }
        // 开始面试
        if (interviewStatus.getState() == 5){
            interviewStatus.setState(6);
            int i = interviewStatusMapper.updateById(interviewStatus);
            if (i != 1){
                log.warn("用户 " + context + " 第一个打开面试 " + interviewStatus + ", 更新面试状态时出现数据库错误");
                return CommonResult.serverError();
            }
        }
        List<InterviewEvaluationAndAnswerPo> interviewEvaluationAndAnswerPos =
                realtimeInterviewMapper.selectAllInterviewEvaluationQnAByBCUID(
                        admission.getId(), context.getUserId(), interviewStatus.getUserId());
        GetInterviewCommentResult result = new GetInterviewCommentResult();
        List<InterviewTablePo> interviewTables = new ArrayList<>();
        for (InterviewEvaluationAndAnswerPo ieaap : interviewEvaluationAndAnswerPos) {
            InterviewTablePo ritp = new InterviewTablePo();
            ritp.setRound(ieaap.getRound());
            ritp.setEditable(maxRound.equals(ieaap.getRound()));
            ritp.setExpectDepartment(ieaap.getExpectDepartment());
            ritp.setRealName(ieaap.getRealName());
            ritp.setIsPass(ieaap.getIsPass());
            List<QuestionAndAnswerPo> rqaas = new ArrayList<>();
            for (InterviewEvaluationQAPo ieqa : ieaap.getQuestions()) {
                QuestionAndAnswerPo rqaa = new QuestionAndAnswerPo();
                rqaa.setOrder(ieqa.getOrder());
                rqaa.setQType(ieqa.getQType());
                rqaa.setType(ieqa.getType());
                rqaa.setQuestion(ieqa.getQuestion());
                rqaa.setQHint(ieqa.getQHint());
                rqaa.setId(ieqa.getId());
                // 1单选，2多选，3下拉框，4输入框，5级联选择器，6量表题
                switch (ieqa.getQType()){
                    case 1:
                    case 2:
                    case 3:
                    case 5:
                        QuestionValueListData qOpts = jsonUtil.deserializationJson(ieqa.getQOpts(),QuestionValueListData.class );
                        if (qOpts == null){
                            log.warn("用户 " + context + " 在纳新 " + admission + " 面试" + interviewStatus + " 中解析面试问题 " + ieqa + " 出现错误");
                            return CommonResult.serverError();
                        }
                        rqaa.setQOpts(qOpts);
                        if (ieqa.getAStr() != null && ieqa.getAStr().length() != 0){
                            MultipleChoiceAnswerPo aSelect = jsonUtil.deserializationJson(ieqa.getAStr(), MultipleChoiceAnswerPo.class);
                            if (aSelect == null){
                                log.warn("用户 " + context + " 在纳新 " + admission + " 面试" + interviewStatus + " 中解析面试问题 " + ieqa + " 的回答 " + ieqa.getAStr() + " 出现错误");
                                return CommonResult.serverError();
                            }
                            rqaa.setASelect(aSelect);
                        }
                        break;
                    case 4:
                        rqaa.setAStr(ieqa.getAStr());
                        break;
                    case 6:
                        rqaa.setAInt(ieqa.getAInt());
                        break;
                }
                rqaas.add(rqaa);
            }
            ritp.setCount(rqaas.size());
            ritp.setQuestions(rqaas);
            interviewTables.add(ritp);
        }
        result.setCount(interviewTables.size());
        return CommonResult.success(result);
    }

    /**
     * 提交/更新面试评价
     *
     * @param param 面试评价
     * @return 处理结果
     */
    @Override
    @Transactional
    public CommonResult<String> postInterviewComment(PostInterviewCommentParam param) {
        BTokenSwapPo context = ThreadLocalContextUtil.getContext();
        Admission admission = interviewCheckMapper.selectOrganizationActivateAdmission(context.getOrganizationId());
        if (admission == null) {
            log.warn("用户 " + context + " 尝试在无活动的纳新时修改面试场地");
            return CommonResult.fail("生成失败：未开始纳新或纳新已结束");
        }
        Integer maxRound = interviewCheckMapper.selectOrganizationActivateInterviewRound(admission.getId());
        if (maxRound == null){
            log.warn("用户 " + context + " 在纳新 " + admission + " 中未查询到任何面试");
            return CommonResult.fail("当前纳新未开启面试");
        }
        InterviewStatus interviewStatus = interviewStatusMapper.selectById(param.getInterview());
        if (interviewStatus == null){
            log.warn("用户 " + context + " 尝试查询不存在的面试 " + param.getInterview() + "的评价信息\n");
            return CommonResult.fail("面试不存在或不属于当前纳新");
        } else if (!Objects.equals(interviewStatus.getAdmissionId(), admission.getId())){
            log.warn("用户 " + context + " 尝试查询不属于当前纳新 " + admission + " 的面试 " + interviewStatus + "的评价信息\n");
            return CommonResult.fail("面试不存在或不属于当前纳新");
        }
        if (interviewStatus.getState() < 3) {
            log.warn("用户 " + context + " 尝试评价未安排的面试 " + interviewStatus);
            return CommonResult.fail("面试未安排");
        }
        if (interviewStatus.getState() > 6) {
            log.warn("用户 " + context + " 尝试评价已结束的面试 " + interviewStatus);
            return CommonResult.fail("面试已结束");
        }
        InterviewEvaluationAndAnswerPo interviewEvaluationAndAnswerPo = realtimeInterviewMapper.selectlatestInterviewEvaluationQnA(interviewStatus.getId(), context.getUserId());
        if (interviewEvaluationAndAnswerPo == null){
            log.warn("不可能，这绝对不可能！用户 " + context + " 在纳新 " + admission + " 面试 " + interviewStatus + " 中查不到面试问题！");
            return CommonResult.serverError();
        }
        InterviewEvaluation interviewEvaluation = interviewEvaluationMapper.selectById(interviewEvaluationAndAnswerPo.getIeId());
        // 对前端返回的结果进行倒序排序
        List<InterviewEvaluationPo> paramList =
                param.getEvaluations().stream()
                        .sorted(Comparator.comparing(InterviewEvaluationPo::getId).reversed())
                        .collect(Collectors.toList());
        // 创建新回答还是修改回答
        if (interviewEvaluation == null){
            interviewEvaluation = new InterviewEvaluation();
            interviewEvaluation.setInterviewStatusId(interviewStatus.getId());
            interviewEvaluation.setRealName((byte) (param.getRealName() ? 1 : 0));
            interviewEvaluation.setUserBId(context.getUserId());
            interviewEvaluation.setIsPass(param.getIsPass());
            interviewEvaluation.setPassDepartmentId(param.getExpectDepartment());
            int i = interviewEvaluationMapper.insert(interviewEvaluation);
            if (i != 1){
                log.warn("用户 " + context + " 在纳新 " + admission + " 面试 " + interviewStatus + " 中插入面试评价状态 " + interviewEvaluation + " 出现数据库错误");
                return CommonResult.serverError();
            }
            List<QuestionScore> insertQs = new ArrayList<>();
            int answer_i = 0;
            List<InterviewEvaluationQAPo> questions = interviewEvaluationAndAnswerPo.getQuestions();
            for (int qi = questions.size() - 1; qi >= 0; qi--){
                boolean kill = false;
                int ai = answer_i;
                for (; ai < paramList.size(); ai++){
                    if (Objects.equals(questions.get(qi).getId(), paramList.get(ai).getId())){
                        answer_i = ai;
                        QuestionScore qs = new QuestionScore();
                        qs.setInterviewStatusId(interviewStatus.getId());
                        qs.setInterviewQuestionId(questions.get(qi).getId());
                        qs.setUserBId(context.getUserId());
                        // 1单选，2多选，3下拉框，4输入框，5级联选择器，6量表题
                        switch (questions.get(qi).getType()){
                            case 1:
                            case 2:
                            case 3:
                            case 5:
                                MultipleChoiceAnswerPo aSelect = paramList.get(ai).getASelect();
                                if (aSelect == null){
                                    kill = true;
                                    break;
                                }
                                String s = jsonUtil.serializationJson(aSelect);
                                if (s == null){
                                    log.warn("用户 " + context + " 在纳新 " + admission + " 面试" + interviewStatus + " 中序列化面试问题 " + paramList.get(ai) + " 的回答 " + aSelect + " 出现错误");
                                    return CommonResult.serverError();
                                }
                                qs.setValue(s);
                                break;
                            case 4:
                                String aStr = paramList.get(ai).getAStr();
                                if (aStr == null || aStr.length() == 0){
                                    kill = true;
                                    break;
                                }
                                qs.setValue(aStr);
                                break;
                            case 6:
                                Integer aInt = paramList.get(ai).getAInt();
                                if (aInt == null){
                                    kill = true;
                                    break;
                                }
                                qs.setScore(aInt);
                                break;
                        }
                        insertQs.add(qs);
                        break;
                    }
                    if (kill) {
                        break;
                    }
                }
                if (kill || ai >= paramList.size()) {
                    log.warn("用户 " + context + " 第一次评价面试 " + interviewStatus + " 回答了 " + param + ", 未回答 " + questions.get(qi));
                    return CommonResult.fail("第一次提交评价须正确回答所有问题");
                }
            }
            if (insertQs.size() != questions.size()){
                log.warn("用户 " + context + " 第一次评价面试 " + interviewStatus + " 回答了 " + param + ", 但所需回答问题为 " + questions);
                return CommonResult.fail("第一次提交评价须正确回答所有问题");
            }
            int insertAll = questionScoreMapper.insertBatch(insertQs);
            if (insertAll != insertQs.size()){
                log.warn("用户 " + context + " 第一次评价面试 " + interviewStatus + " 回答 " + param + ", 评价为 " + insertQs + "时插入数据库数量不符合预期 " + insertAll);
                return CommonResult.serverError();
            }
        } else {
            // 是否需要更新面试评价信息
            boolean updateie = false;
            if (param.getRealName() != null && param.getRealName() != (interviewEvaluation.getRealName().intValue() == 1)) {
                updateie = true;
                interviewEvaluation.setRealName((byte) (param.getRealName() ? 1 : 0));
            }
            if (param.getExpectDepartment() != null && !Objects.equals(param.getExpectDepartment(), interviewEvaluation.getPassDepartmentId())) {
                updateie = true;
                interviewEvaluation.setPassDepartmentId(param.getExpectDepartment());
            }
            if (param.getIsPass() != null && !Objects.equals(param.getIsPass(), interviewEvaluation.getIsPass())) {
                updateie = true;
                interviewEvaluation.setIsPass(param.getIsPass());
            }
            if (updateie) {
                int i = interviewEvaluationMapper.updateById(interviewEvaluation);
                if (i != 1) {
                    log.warn("用户 " + context + " 在面试 " + interviewStatus + " 中更新面试评价状态 " + interviewEvaluation + " 出现数据库错误");
                    return CommonResult.serverError();
                }
            }
            List<QuestionScore> updateQs = new ArrayList<>();
            int answer_i = 0;
            List<InterviewEvaluationQAPo> questions = interviewEvaluationAndAnswerPo.getQuestions();
            for (int qi = questions.size() - 1; qi >= 0; qi--) {
                int ai = answer_i;
                for (; ai < paramList.size(); ai++) {
                    if (Objects.equals(questions.get(qi).getId(), paramList.get(ai).getId())) {
                        answer_i = ai;
                        boolean update = true;
                        QuestionScore qs = new QuestionScore();
                        qs.setInterviewStatusId(interviewStatus.getId());
                        qs.setInterviewQuestionId(questions.get(qi).getId());
                        qs.setUserBId(context.getUserId());
                        // 1单选，2多选，3下拉框，4输入框，5级联选择器，6量表题
                        switch (questions.get(qi).getType()) {
                            case 1:
                            case 2:
                            case 3:
                            case 5:
                                MultipleChoiceAnswerPo aSelect = paramList.get(ai).getASelect();
                                if (aSelect == null) {
                                    break;
                                }
                                String s = jsonUtil.serializationJson(aSelect);
                                if (s == null) {
                                    log.warn("用户 " + context + " 在纳新 " + admission + " 面试" + interviewStatus + " 中序列化面试问题 " + paramList.get(ai) + " 的回答 " + aSelect + " 出现错误");
                                    break;
                                }
                                if (!s.equals(questions.get(qi).getAStr())) {
                                    qs.setValue(s);
                                } else {
                                    update = false;
                                }
                                break;
                            case 4:
                                String aStr = paramList.get(ai).getAStr();
                                if (aStr == null) {
                                    break;
                                }
                                if (!aStr.equals(questions.get(qi).getAStr())) {
                                    qs.setValue(aStr);
                                } else {
                                    update = false;
                                }
                                break;
                            case 6:
                                Integer aInt = paramList.get(ai).getAInt();
                                if (aInt == null) {
                                    break;
                                }
                                if (!aInt.equals(questions.get(qi).getAInt())) {
                                    qs.setScore(aInt);
                                } else {
                                    update = false;
                                }
                                break;
                        }
                        if (update) {
                            updateQs.add(qs);
                        }
                        break;
                    }
                }
            }
            for (QuestionScore updateQ : updateQs) {
                int i = questionScoreMapper.updateById(updateQ);
                if (i != 1) {
                    log.warn("用户 " + context + "在面试 " + interviewStatus + " 中更新面试评价 " + param + " 时出现数据库异常，受影响的评价为 " + updateQ);
                    return CommonResult.serverError();
                }
            }
        }
        return CommonResult.success();
    }
}

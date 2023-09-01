package com.sipc.mmtbackend.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sipc.mmtbackend.mapper.InterviewCheckMapper;
import com.sipc.mmtbackend.mapper.InterviewStatusMapper;
import com.sipc.mmtbackend.mapper.RealtimeInterviewMapper;
import com.sipc.mmtbackend.pojo.domain.Admission;
import com.sipc.mmtbackend.pojo.domain.AdmissionAddress;
import com.sipc.mmtbackend.pojo.domain.InterviewStatus;
import com.sipc.mmtbackend.pojo.domain.po.RealtimeInterviewPo.InterviewEvaluationAndAnswerPo;
import com.sipc.mmtbackend.pojo.domain.po.RealtimeInterviewPo.InterviewEvaluationQAPo;
import com.sipc.mmtbackend.pojo.domain.po.RealtimeInterviewPo.InterviewStatusPo;
import com.sipc.mmtbackend.pojo.domain.po.RealtimeInterviewPo.ProgressBarPo;
import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.pojo.dto.data.QuestionValueListData;
import com.sipc.mmtbackend.pojo.dto.param.RealtimeInterview.FinishInterviewParam;
import com.sipc.mmtbackend.pojo.dto.param.RealtimeInterview.PutInterviewPlaceParam;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Slf4j
public class RealtimeInterviewServiceImpl implements RealtimeInterviewService {
    private final InterviewCheckMapper interviewCheckMapper;
    private final RealtimeInterviewMapper realtimeInterviewMapper;
    private final CheckinQRCodeUtil checkinQRCodeUtil;
    private final InterviewStatusMapper interviewStatusMapper;
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
            log.warn("用户 " + context + " 尝试查询不存在的面试 " + interviewStatus + "的评价信息\n");
            return CommonResult.fail("面试不存在或不属于当前纳新");
        } else if (!Objects.equals(interviewStatus.getAdmissionId(), admission.getId())){
            log.warn("用户 " + context + " 尝试查询不属于当前纳新 " + admission + " 的面试 " + interviewStatus + "的评价信息\n");
            return CommonResult.fail("面试不存在或不属于当前纳新");
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
            List<QuestionAndAnswerPo> rqaas = new ArrayList<>();
            for (InterviewEvaluationQAPo ieqa : ieaap.getQuestions()) {
                QuestionAndAnswerPo rqaa = new QuestionAndAnswerPo();
                rqaa.setOrder(ieqa.getOrder());
                rqaa.setQType(ieqa.getQType());
                rqaa.setType(ieqa.getType());
                rqaa.setQuestion(ieqa.getQuestion());
                rqaa.setQHint(ieqa.getQHint());
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
}

package com.sipc.mmtbackend.service.impl;

import com.sipc.mmtbackend.mapper.InterviewCheckMapper;
import com.sipc.mmtbackend.pojo.domain.Admission;
import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.pojo.dto.enums.InterviewRoundEnum;
import com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardMiddleResult.GetInterviewRoundsResult;
import com.sipc.mmtbackend.pojo.dto.result.po.KVPo;
import com.sipc.mmtbackend.service.InterviewBoardMiddleService;
import com.sipc.mmtbackend.utils.CheckroleBUtil.pojo.BTokenSwapPo;
import com.sipc.mmtbackend.utils.ThreadLocalContextUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Slf4j
public class InterviewBoardMiddleServiceImpl implements InterviewBoardMiddleService {
    private final InterviewCheckMapper interviewCheckMapper;

    /**
     * 获取当前组织已开始的面试轮次
     *
     * @return 面试轮次
     */
    @Override
    public CommonResult<GetInterviewRoundsResult> getInterviewRounds() {
        BTokenSwapPo context = ThreadLocalContextUtil.getContext();
        Admission admission = interviewCheckMapper.selectOrganizationActivateAdmission(context.getOrganizationId());
        if (admission == null) {
            log.warn("用户 " + context + " 尝试在无活动的纳新时查询已开始面试轮次");
            return CommonResult.fail("查询失败：未开始纳新或纳新已结束");
        }
        Integer maxRound = interviewCheckMapper.selectOrganizationActivateInterviewRound(admission.getId());
        if (maxRound == null){
            log.warn("用户 " + context + " 在纳新 " + admission + " 中未查询到任何面试");
            return CommonResult.fail("当前纳新未开启面试");
        }
        GetInterviewRoundsResult result = new GetInterviewRoundsResult();
        result.setCount(maxRound);
        List<KVPo> results = new ArrayList<>();
        for (int i = 1; i <= maxRound; i++){
            InterviewRoundEnum round = InterviewRoundEnum.checkRound(i);
            if (round == null){
                log.warn("用户 " + context + " 在纳新 " + admission + " 中查询到非法面试轮次" + i);
                return CommonResult.serverError();
            }
            results.add(new KVPo(i, round.getName()));
        }
        result.setRounds(results);
        return CommonResult.success(result);
    }
}

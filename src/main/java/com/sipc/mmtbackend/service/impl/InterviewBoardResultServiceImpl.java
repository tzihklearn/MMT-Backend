package com.sipc.mmtbackend.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sipc.mmtbackend.mapper.DepartmentMapper;
import com.sipc.mmtbackend.mapper.InterviewBoardRDataMapper;
import com.sipc.mmtbackend.mapper.InterviewCheckMapper;
import com.sipc.mmtbackend.pojo.domain.Admission;
import com.sipc.mmtbackend.pojo.domain.Department;
import com.sipc.mmtbackend.pojo.domain.po.InterviewBoardRPo.InterviewScoreAndRankPo;
import com.sipc.mmtbackend.pojo.domain.po.InterviewBoardRPo.DepartmentPassedCountPo;
import com.sipc.mmtbackend.pojo.domain.po.InterviewBoardRPo.InterviewResultData;
import com.sipc.mmtbackend.pojo.domain.po.InterviewBoardRPo.LineChartLineDataDaoPo;
import com.sipc.mmtbackend.pojo.domain.po.InterviewBoardRPo.OrderPassedCountPo;
import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.pojo.dto.enums.InterviewRoundEnum;
import com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardResultResult.*;
import com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardResultResult.po.DepartmentPassCountPo;
import com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardResultResult.po.GetPassCountGroupByDepartmentResult;
import com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardResultResult.po.OrderPassCountPo;
import com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardResultResult.po.RankAndScorePo;
import com.sipc.mmtbackend.pojo.dto.result.po.LineChartLineDataPo;
import com.sipc.mmtbackend.service.InterviewBoardResultService;
import com.sipc.mmtbackend.utils.CheckroleBUtil.pojo.BTokenSwapPo;
import com.sipc.mmtbackend.utils.ThreadLocalContextUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Slf4j
public class InterviewBoardResultServiceImpl implements InterviewBoardResultService {
    private final InterviewBoardRDataMapper interviewBoardRDataMapper;
    private final InterviewCheckMapper interviewCheckMapper;
    private final DepartmentMapper departmentMapper;

    /**
     * 获取面试最终数据
     *
     * @param departmentId 部门ID
     * @return 面试最终数据
     */
    @Override
    public CommonResult<GetInterviewResultDataResult> getResultData(int departmentId) {
        BTokenSwapPo context = ThreadLocalContextUtil.getContext();
        Admission admission = interviewCheckMapper.selectOrganizationActivateAdmission(context.getOrganizationId());
        if (admission == null) {
            log.warn("用户 " + context + " 尝试在无活动的纳新时查询面试最终数据");
            return CommonResult.fail("查询失败：未开始纳新或纳新已结束");
        }
        Integer maxRound = interviewCheckMapper.selectOrganizationActivateInterviewRound(admission.getId());
        if (maxRound == null){
            log.warn("用户 " + context + " 在纳新 " + admission + " 中未查询到任何面试");
            return CommonResult.fail("当前纳新未开启面试");
        }
        if (departmentId != 0){
            Department department = departmentMapper.selectById(departmentId);
            if (department == null){
                log.warn("用户 " + context + " 在纳新 " + admission + " 请求不存在的部门" + departmentId + "的面试最终数据");
                return CommonResult.fail("部门不存在或不属于当前组织");
            } else if (!Objects.equals(department.getOrganizationId(), context.getOrganizationId())){
                log.warn("用户 " + context + " 在纳新 " + admission + " 请求不属于其组织的的部门" + department + "的面试最终数据");
                return CommonResult.fail("部门不存在或不属于当前组织");
            }
        }
        GetInterviewResultDataResult result = new GetInterviewResultDataResult();
        InterviewResultData interviewResultData = interviewBoardRDataMapper.selectInterviewResultData(maxRound, admission.getId(), departmentId);
        result.setCheckinCount(interviewResultData.getCheckinCount());
        result.setPassedCount(interviewResultData.getPassedCount());
        result.setSignupCount(interviewResultData.getSignupCount());
        result.setLastPassedCount(interviewResultData.getLastPassedCount());
        return CommonResult.success(result);
    }

    /**
     * 获取最终各部门通过人数（组织饼图）
     *
     * @return 各部门通过人数
     */
    @Override
    public CommonResult<GetDepartmentPassCountResult> getDepartmentPassCount() {
        BTokenSwapPo context = ThreadLocalContextUtil.getContext();
        Admission admission = interviewCheckMapper.selectOrganizationActivateAdmission(context.getOrganizationId());
        if (admission == null) {
            log.warn("用户 " + context + " 尝试在无活动的纳新时查询面试最终数据");
            return CommonResult.fail("查询失败：未开始纳新或纳新已结束");
        }
        Integer maxRound = interviewCheckMapper.selectOrganizationActivateInterviewRound(admission.getId());
        if (maxRound == null){
            log.warn("用户 " + context + " 在纳新 " + admission + " 中未查询到任何面试");
            return CommonResult.fail("当前纳新未开启面试");
        }
        List<DepartmentPassedCountPo> countPos = interviewBoardRDataMapper.selectPassedCountPerDepartment(maxRound, admission.getId());
        GetDepartmentPassCountResult result = new GetDepartmentPassCountResult();
        List<DepartmentPassCountPo> results = new ArrayList<>();
        for (DepartmentPassedCountPo po : countPos) {
            DepartmentPassCountPo dpcp = new DepartmentPassCountPo();
            dpcp.setCount(po.getCount());
            dpcp.setId(po.getId());
            dpcp.setName(po.getName());
            results.add(dpcp);
        }
        result.setCount(results.size());
        result.setDepartments(results);
        return CommonResult.success(result);
    }

    /**
     * 获取不同部门通过人数随面试轮次变化折线图（组织折线图）
     *
     * @return 不同部门通过人数随时间变化折线图数据
     */
    @Override
    public CommonResult<GetPassCountGroupByDepartmentResult> getPassCountGroupByDepartmentLineChart() {
        BTokenSwapPo context = ThreadLocalContextUtil.getContext();
        Admission admission = interviewCheckMapper.selectOrganizationActivateAdmission(context.getOrganizationId());
        if (admission == null) {
            log.warn("用户 " + context + " 尝试在无活动的纳新时查询面试最终数据");
            return CommonResult.fail("查询失败：未开始纳新或纳新已结束");
        }
        Integer maxRound = interviewCheckMapper.selectOrganizationActivateInterviewRound(admission.getId());
        if (maxRound == null){
            log.warn("用户 " + context + " 在纳新 " + admission + " 中未查询到任何面试");
            return CommonResult.fail("当前纳新未开启面试");
        }
        List<LineChartLineDataDaoPo> lineDataDaoPos = interviewBoardRDataMapper.selectPassedCountLineChartGroupByRoundByAdmissionId(
                admission.getId());
        GetPassCountGroupByDepartmentResult result = new GetPassCountGroupByDepartmentResult();
        List<LineChartLineDataPo> results = new ArrayList<>();
        if (lineDataDaoPos.size() == 0){
            result.setDepartments(results);
            result.setRound(new ArrayList<>());
            return CommonResult.success(result);
        }
        List<String> abscissaData = lineDataDaoPos.get(0).getAbscissaData();
        if (abscissaData == null){
            log.warn("数据库错误：用户 " + context + " 获取不同部门通过人数随面试轮次变化折线图（组织折线图）时查询到非法面试轮次，相关查询结果如下：\n" + lineDataDaoPos);
            return CommonResult.serverError();
        }
        result.setRound(abscissaData);
        for (LineChartLineDataDaoPo data : lineDataDaoPos) {
            LineChartLineDataPo depData = new LineChartLineDataPo();
            depData.setName(data.getName());
            depData.setData(data.getYDatas());
            results.add(depData);
        }
        result.setDepartments(results);
        return CommonResult.success(result);
    }

    /**
     * 获取最终各志愿通过人数（部门饼图）
     *
     * @param departmentId 部门ID
     * @return 最终各志愿通过人数
     */
    @Override
    public CommonResult<GetOrderPassCountResult> getOrderPassCount(int departmentId) {
        BTokenSwapPo context = ThreadLocalContextUtil.getContext();
        Admission admission = interviewCheckMapper.selectOrganizationActivateAdmission(context.getOrganizationId());
        if (admission == null) {
            log.warn("用户 " + context + " 尝试在无活动的纳新时查询面试最终数据");
            return CommonResult.fail("查询失败：未开始纳新或纳新已结束");
        }
        Integer maxRound = interviewCheckMapper.selectOrganizationActivateInterviewRound(admission.getId());
        if (maxRound == null){
            log.warn("用户 " + context + " 在纳新 " + admission + " 中未查询到任何面试");
            return CommonResult.fail("当前纳新未开启面试");
        }
        Department department = departmentMapper.selectById(departmentId);
        if (department == null) {
            log.warn("用户 " + context + " 在纳新 " + admission + " 请求不存在的部门" + departmentId + "的面试最终数据");
            return CommonResult.fail("部门不存在或不属于当前组织");
        } else if (!Objects.equals(department.getOrganizationId(), context.getOrganizationId())) {
            log.warn("用户 " + context + " 在纳新 " + admission + " 请求不属于其组织的的部门" + department + "的面试最终数据");
            return CommonResult.fail("部门不存在或不属于当前组织");
        }
        List<OrderPassedCountPo> countPos = interviewBoardRDataMapper.selectPassedCountPerOrder(maxRound, admission.getId(), departmentId);
        GetOrderPassCountResult result = new GetOrderPassCountResult();
        List<OrderPassCountPo> results = new ArrayList<>();
        for (OrderPassedCountPo countPo : countPos) {
            OrderPassCountPo opcp = new OrderPassCountPo();
            opcp.setId(countPo.getId());
            opcp.setCount(countPo.getCount());
            InterviewRoundEnum interviewRoundEnum = InterviewRoundEnum.checkRound(countPo.getId());
            if (interviewRoundEnum == null){
                log.warn("数据库错误：用户 " + context
                        + " 获取最终各志愿通过人数（部门饼图）时查询到非法面试轮次，相关查询结果如下：\n"
                        + countPos);
                return CommonResult.serverError();
            }
            opcp.setName(interviewRoundEnum.getName());
            results.add(opcp);
        }
        result.setCount(results.size());
        result.setOrders(results);
        return CommonResult.success(result);
    }

    /**
     * 获取不同志愿通过人数随时间变化折线图（部门折线图）
     *
     * @param departmentId 组织ID
     * @return 不同志愿通过人数随时间变化情况
     */
    @Override
    public CommonResult<GetPassCountGroupByOrderLineChartResult> getPassCountGroupByOrderLineChart(int departmentId) {
        BTokenSwapPo context = ThreadLocalContextUtil.getContext();
        Admission admission = interviewCheckMapper.selectOrganizationActivateAdmission(context.getOrganizationId());
        if (admission == null) {
            log.warn("用户 " + context + " 尝试在无活动的纳新时查询面试最终数据");
            return CommonResult.fail("查询失败：未开始纳新或纳新已结束");
        }
        Integer maxRound = interviewCheckMapper.selectOrganizationActivateInterviewRound(admission.getId());
        if (maxRound == null){
            log.warn("用户 " + context + " 在纳新 " + admission + " 中未查询到任何面试");
            return CommonResult.fail("当前纳新未开启面试");
        }
        Department department = departmentMapper.selectById(departmentId);
        if (department == null) {
            log.warn("用户 " + context + " 在纳新 " + admission + " 请求不存在的部门" + departmentId + "的面试最终数据");
            return CommonResult.fail("部门不存在或不属于当前组织");
        } else if (!Objects.equals(department.getOrganizationId(), context.getOrganizationId())) {
            log.warn("用户 " + context + " 在纳新 " + admission + " 请求不属于其组织的的部门" + department + "的面试最终数据");
            return CommonResult.fail("部门不存在或不属于当前组织");
        }
        List<LineChartLineDataDaoPo> chartLineDataDaoPos = interviewBoardRDataMapper.selectPassedCountLineChartGroupByRoundAndOrderByAdmissionId(
                admission.getId(), departmentId
        );
        GetPassCountGroupByOrderLineChartResult result = new GetPassCountGroupByOrderLineChartResult();
        List<LineChartLineDataPo> results = new ArrayList<>();
        if (chartLineDataDaoPos.size() == 0){
            result.setOrders(results);
            result.setRound(new ArrayList<>());
            return CommonResult.success(result);
        }
        List<String> abscissaData = chartLineDataDaoPos.get(0).getAbscissaData();
        if (abscissaData == null){
            log.warn("数据库错误：用户 " + context
                    + " 获取不同志愿通过人数随面试轮次变化折线图（部门折线图）时查询到非法面试轮次，相关查询结果如下：\n"
                    + chartLineDataDaoPos);
            return CommonResult.serverError();
        }
        result.setRound(abscissaData);
        for (LineChartLineDataDaoPo data : chartLineDataDaoPos) {
            LineChartLineDataPo depData = new LineChartLineDataPo();
            depData.setName(data.getName());
            depData.setData(data.getYDatas());
            results.add(depData);
        }
        result.setOrders(results);
        return CommonResult.success(result);
    }

    /**
     * 获取面试通过者排名与分数
     *
     * @param departmentId 部门ID, 默认0
     * @param pageId       页码，默认1
     * @return 通过面试者排名与分数
     */
    @Override
    public CommonResult<GetPassedRankAndScoreResult> getPassedRankAndScore(int departmentId, int pageId) {
        BTokenSwapPo context = ThreadLocalContextUtil.getContext();
        Admission admission = interviewCheckMapper.selectOrganizationActivateAdmission(context.getOrganizationId());
        if (admission == null) {
            log.warn("用户 " + context + " 尝试在无活动的纳新时查询面试最终数据");
            return CommonResult.fail("查询失败：未开始纳新或纳新已结束");
        }
        Integer maxRound = interviewCheckMapper.selectOrganizationActivateInterviewRound(admission.getId());
        if (maxRound == null){
            log.warn("用户 " + context + " 在纳新 " + admission + " 中未查询到任何面试");
            return CommonResult.fail("当前纳新未开启面试");
        }
        if (departmentId != 0){
            Department department = departmentMapper.selectById(departmentId);
            if (department == null){
                log.warn("用户 " + context + " 在纳新 " + admission + " 请求不存在的部门" + departmentId + "的面试最终数据");
                return CommonResult.fail("部门不存在或不属于当前组织");
            } else if (!Objects.equals(department.getOrganizationId(), context.getOrganizationId())){
                log.warn("用户 " + context + " 在纳新 " + admission + " 请求不属于其组织的的部门" + department + "的面试最终数据");
                return CommonResult.fail("部门不存在或不属于当前组织");
            }
        }
        Page<InterviewScoreAndRankPo> page = new Page<>(pageId, 15);
        IPage<InterviewScoreAndRankPo> iPage = interviewBoardRDataMapper.selectPassedInterviewScoreAndRank(
                page, maxRound, admission.getId(), departmentId);
        GetPassedRankAndScoreResult result = new GetPassedRankAndScoreResult();
        List<RankAndScorePo> results = new ArrayList<>();
        for (InterviewScoreAndRankPo isar : iPage.getRecords()) {
            RankAndScorePo ras = new RankAndScorePo();
            ras.setName(isar.getName());
            ras.setRank(isar.getRank());
            ras.setScore(isar.getScore());
            results.add(ras);
        }
        result.setScore(results);
        result.setCount(results.size());
        result.setPages((int) iPage.getPages());
        return CommonResult.success(result);
    }
}

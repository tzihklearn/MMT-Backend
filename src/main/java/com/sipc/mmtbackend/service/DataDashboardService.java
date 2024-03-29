package com.sipc.mmtbackend.service;

import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.pojo.dto.param.dataDashboard.EvaluationChangeParam;
import com.sipc.mmtbackend.pojo.dto.param.dataDashboard.SiftParam;
import com.sipc.mmtbackend.pojo.dto.result.DataDashboardExportResult;
import com.sipc.mmtbackend.pojo.dto.result.dataDashboard.DataDashboardInfoResult;
import com.sipc.mmtbackend.pojo.dto.result.dataDashboard.EvaluationInfoResult;
import com.sipc.mmtbackend.pojo.dto.result.dataDashboard.ResumeInfoResult;
import com.sipc.mmtbackend.pojo.dto.result.dataDashboard.RoundResult;

public interface DataDashboardService {

    CommonResult<DataDashboardInfoResult> all(Integer page, Integer pageNum);

    CommonResult<DataDashboardInfoResult> sift(SiftParam siftParam, Integer page, Integer pageNum);

    CommonResult<DataDashboardExportResult> export(SiftParam siftParam);

    CommonResult<ResumeInfoResult> resume(Integer id);

    CommonResult<EvaluationInfoResult> evaluationInfo(Integer id, Integer round);

    CommonResult<String> changeEvaluation(EvaluationChangeParam evaluationChangeParam);

    CommonResult<RoundResult> round(Integer id);

}

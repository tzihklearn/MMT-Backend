package com.sipc.mmtbackend.service;

import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.pojo.dto.param.dataDashboard.SiftParam;
import com.sipc.mmtbackend.pojo.dto.result.DataDashboardExportResult;
import com.sipc.mmtbackend.pojo.dto.result.dataDashboard.DataDashboardInfoResult;
import com.sipc.mmtbackend.pojo.dto.result.dataDashboard.ResumeInfoResult;
import org.springframework.web.bind.annotation.RequestParam;

public interface DataDashboardService {

    CommonResult<DataDashboardInfoResult> all(Integer page, Integer pageNum);

    CommonResult<DataDashboardInfoResult> sift(SiftParam siftParam, Integer page, Integer pageNum);

    CommonResult<DataDashboardExportResult> export(SiftParam siftParam);

    CommonResult<ResumeInfoResult> resume(Integer id);

}

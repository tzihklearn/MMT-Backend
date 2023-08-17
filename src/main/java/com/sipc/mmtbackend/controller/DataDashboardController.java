package com.sipc.mmtbackend.controller;

import com.sipc.mmtbackend.annotation.BPermission;
import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.pojo.dto.param.dataDashboard.SiftParam;
import com.sipc.mmtbackend.pojo.dto.result.DataDashboardExportResult;
import com.sipc.mmtbackend.pojo.dto.result.dataDashboard.DataDashboardInfoResult;
import com.sipc.mmtbackend.service.DataDashboardService;
import com.sipc.mmtbackend.utils.CheckroleBUtil.pojo.PermissionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author tzih
 * @version v1.0
 * @since 2023.05.31
 */
@RestController
@RequestMapping("/b/data/dashboard")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@BPermission(PermissionEnum.MEMBER)
public class DataDashboardController {

    private final DataDashboardService dataDashboardService;

    @GetMapping("/all")
    public CommonResult<DataDashboardInfoResult> all(@RequestParam Integer page, @RequestParam Integer pageNum) {
        return dataDashboardService.all(page, pageNum);
    }

    @PostMapping("/sift")
    public CommonResult<DataDashboardInfoResult> sift(@RequestBody SiftParam siftParam, @RequestParam Integer page, @RequestParam Integer pageNum) {
        return dataDashboardService.sift(siftParam, page, pageNum);
    }

    @PostMapping("/export")
    public CommonResult<DataDashboardExportResult> export(@RequestBody(required = false) SiftParam siftParam) {
        return dataDashboardService.export(siftParam);
    }

    @PostMapping("/test")
    public CommonResult<String> test() throws RuntimeException {
        throw new RuntimeException("test测试飞书机器人");
//        return null;
//        return null;
    }

}
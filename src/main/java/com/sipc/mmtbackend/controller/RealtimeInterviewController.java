package com.sipc.mmtbackend.controller;

import com.sipc.mmtbackend.annotation.BPermission;
import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.pojo.dto.result.RealtimeIntreviewdResult.GetInterviewPlacesResult;
import com.sipc.mmtbackend.pojo.dto.result.RealtimeIntreviewdResult.GetRealtimeProgressBarResult;
import com.sipc.mmtbackend.service.RealtimeInterviewService;
import com.sipc.mmtbackend.utils.CheckroleBUtil.pojo.PermissionEnum;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/b/interview/realtime")
@BPermission(PermissionEnum.MEMBER)
public class RealtimeInterviewController {
    @Resource
    RealtimeInterviewService realtimeInterviewService;

    /**
     * 获取面试进度条
     *
     * @param placeId 面试场地 ID
     * @return 面试进度条数据
     */
    @GetMapping("/progressBar")
    CommonResult<GetRealtimeProgressBarResult> getTodayInterviewProgressBar(
            @RequestParam(value = "place", defaultValue = "0") int placeId){
        return null;
    }

    /**
     * 获取签到二维码
     *
     * @return 签到二维码的 Base64 编码
     */
    @GetMapping("/qrcode")
    CommonResult<String> getCheckInQRCode(){
        return null;
    }

    /**
     * 获取面试场地
     *
     * @return 所有面试场地
     */
    @GetMapping("/places")
    CommonResult<GetInterviewPlacesResult> getINterviewPlaces(){
        return null;
    }
}

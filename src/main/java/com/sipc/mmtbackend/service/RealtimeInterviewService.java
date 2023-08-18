package com.sipc.mmtbackend.service;

import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.pojo.dto.param.RealtimeInterview.FinishInterviewParam;
import com.sipc.mmtbackend.pojo.dto.result.RealtimeIntreviewdResult.GetInterviewPlacesResult;
import com.sipc.mmtbackend.pojo.dto.result.RealtimeIntreviewdResult.GetInterviewProgressBarResult;

public interface RealtimeInterviewService {
    /**
     * 获取签到二维码
     *
     * @return 签到二维码的 Base64 编码
     */
    CommonResult<String> getCheckinQRCode();

    /**
     * 获取面试场地
     *
     * @return 所有面试场地
     */
    CommonResult<GetInterviewPlacesResult> getInterviewPlaces();

    /**
     * 获取面试进度条
     *
     * @param placeId 面试场地 ID
     * @return 面试进度条数据
     */
    CommonResult<GetInterviewProgressBarResult> getInterviewProgressBar(int placeId);

    /**
     * 结束面试
     *
     * @param param 面试ID
     * @return 处理结果
     */
    CommonResult<String> finishInterview(FinishInterviewParam param);
}

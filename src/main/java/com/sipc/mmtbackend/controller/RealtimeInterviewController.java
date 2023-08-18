package com.sipc.mmtbackend.controller;

import com.sipc.mmtbackend.annotation.BPermission;
import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.pojo.dto.param.RealtimeInterview.FinishInterviewParam;
import com.sipc.mmtbackend.pojo.dto.param.RealtimeInterview.PostInterviewCommentParam;
import com.sipc.mmtbackend.pojo.dto.result.RealtimeIntreviewdResult.GetInterviewCommentResult;
import com.sipc.mmtbackend.pojo.dto.result.RealtimeIntreviewdResult.GetInterviewPlacesResult;
import com.sipc.mmtbackend.pojo.dto.result.RealtimeIntreviewdResult.GetInterviewProgressBarResult;
import com.sipc.mmtbackend.pojo.dto.result.RealtimeIntreviewdResult.GetIntervieweeListResult;
import com.sipc.mmtbackend.service.RealtimeInterviewService;
import com.sipc.mmtbackend.utils.CheckroleBUtil.pojo.PermissionEnum;
import org.springframework.web.bind.annotation.*;

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
    public CommonResult<GetInterviewProgressBarResult> getInterviewProgressBar(
            @RequestParam(value = "place", defaultValue = "0") int placeId){
        return realtimeInterviewService.getInterviewProgressBar(placeId);
    }

    /**
     * 获取签到二维码
     *
     * @return 签到二维码的 Base64 编码
     */
    @GetMapping("/qrcode")
    public CommonResult<String> getCheckinQRCode(){
        return realtimeInterviewService.getCheckinQRCode();
    }

    /**
     * 获取面试场地
     *
     * @return 所有面试场地
     */
    @GetMapping("/places")
    public CommonResult<GetInterviewPlacesResult> getInterviewPlaces(){
        return realtimeInterviewService.getInterviewPlaces();
    }

    /**
     * 获取面试人员名单
     *
     * @param page 第几页
     * @param keyword 搜索关键词
     * @param placeId 面试场地ID
     * @return  被面试这名单
     */
    @GetMapping("/interviewee")
    public CommonResult<GetIntervieweeListResult> getIntervieweeList(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "keyword", defaultValue = "") String keyword,
            @RequestParam(value = "place", defaultValue = "0") int placeId
    ){
        return realtimeInterviewService.getIntervieweeList(page, keyword, placeId);
    }

    /**
     * 获取本轮与已结束轮次面试评价问题与回答
     *
     * @param interview 本论面试ID
     * @return 面试评价
     */
    @GetMapping("/comment")
    public CommonResult<GetInterviewCommentResult> getInterviewComment(
            @RequestParam("interview") Integer interview
    ){
        return null;
    }

    /**
     * 提交/更新面试评价
     *
     * @param param 面试评价
     * @return 处理结果
     */
    @PutMapping("/comment")
    public CommonResult<String> postInterviewComment(@RequestBody PostInterviewCommentParam param){
        return null;
    }

    /**
     * 结束面试
     *
     * @param param 面试ID
     * @return 处理结果
     */
    @PostMapping("/finish")
    public CommonResult<String> finishInterview(
            @RequestBody FinishInterviewParam param
    ){
        return realtimeInterviewService.finishInterview(param);
    }
}

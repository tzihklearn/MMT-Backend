package com.sipc.mmtbackend.controller;

import com.sipc.mmtbackend.annotation.BPermission;
import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.pojo.dto.param.interviewreview.ArrangeParam;
import com.sipc.mmtbackend.pojo.dto.param.interviewreview.SendParam;
import com.sipc.mmtbackend.pojo.dto.param.interviewreview.SiftParam;
import com.sipc.mmtbackend.pojo.dto.result.interviewreview.*;
import com.sipc.mmtbackend.pojo.exceptions.DateBaseException;
import com.sipc.mmtbackend.service.InterviewReviewService;
import com.sipc.mmtbackend.utils.CheckroleBUtil.pojo.PermissionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author tzih
 * @version v1.0
 * @since 2023.08.28
 */
@RestController
@RequestMapping("/b/interview/review")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@BPermission(PermissionEnum.MEMBER)
public class InterviewReviewController {

    private final InterviewReviewService interviewReviewService;

    @GetMapping("/address/all")
    public CommonResult<AddressResult> getAddress() {
        return interviewReviewService.getAddress();
    }

    @GetMapping("/info/all")
    public CommonResult<InfoAllResult> all(@RequestParam Integer page) {
        return interviewReviewService.all(page);
    }

    @PostMapping("/info/sift")
    public CommonResult<InfoAllResult> sift(@RequestBody SiftParam siftParam) {
        return interviewReviewService.sift(siftParam);
    }

    @GetMapping("/pie/chat/info")
    public CommonResult<PieChatResult> pieChatInfo() {
        return interviewReviewService.pieChatInfo();
    }

    @PostMapping("/arrange")
    public CommonResult<String> arrange(@RequestBody ArrangeParam arrangeParam) throws DateBaseException {
        return interviewReviewService.arrange(arrangeParam);
    }

    @GetMapping("/message/template")
    public CommonResult<MessageTemplateResult> messageTemplate() {
        return interviewReviewService.messageTemplate();
    }

    @GetMapping("/message/num")
    public CommonResult<MessageNumResult> messageNum(@RequestParam Integer status) {
        return interviewReviewService.messageNum(status);
    }

    @PostMapping("/message/send")
    public CommonResult<String> send(@RequestBody SendParam sendParam) throws DateBaseException {
        return interviewReviewService.send(sendParam);
    }

}

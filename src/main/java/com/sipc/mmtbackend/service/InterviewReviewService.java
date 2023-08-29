package com.sipc.mmtbackend.service;

import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.pojo.dto.param.interviewreview.ArrangeParam;
import com.sipc.mmtbackend.pojo.dto.param.interviewreview.SendParam;
import com.sipc.mmtbackend.pojo.dto.param.interviewreview.SiftParam;
import com.sipc.mmtbackend.pojo.dto.result.interviewreview.AddressResult;
import com.sipc.mmtbackend.pojo.dto.result.interviewreview.InfoAllResult;
import com.sipc.mmtbackend.pojo.dto.result.interviewreview.MessageTemplateResult;
import com.sipc.mmtbackend.pojo.dto.result.interviewreview.PieChatResult;
import com.sipc.mmtbackend.pojo.exceptions.DateBaseException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

public interface InterviewReviewService {

    CommonResult<AddressResult> getAddress();

    CommonResult<InfoAllResult> all(Integer page);

    CommonResult<InfoAllResult> sift(SiftParam siftParam);

    CommonResult<PieChatResult> pieChatInfo(Integer departmentId, Integer addressId);

    CommonResult<String> arrange(ArrangeParam arrangeParam) throws DateBaseException;

    CommonResult<MessageTemplateResult> messageTemplate(Byte status);

    CommonResult<String> send(SendParam sendParam) throws DateBaseException;

}

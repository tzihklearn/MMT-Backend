package com.sipc.mmtbackend.service;

import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.pojo.dto.param.interviewreview.ArrangeParam;
import com.sipc.mmtbackend.pojo.dto.param.interviewreview.SendParam;
import com.sipc.mmtbackend.pojo.dto.param.interviewreview.SiftParam;
import com.sipc.mmtbackend.pojo.dto.result.interviewreview.*;
import com.sipc.mmtbackend.pojo.exceptions.DateBaseException;

public interface InterviewReviewService {

    CommonResult<AddressResult> getAddress();

    CommonResult<InfoAllResult> all(Integer page);

    CommonResult<InfoAllResult> sift(SiftParam siftParam);

    CommonResult<PieChatResult> pieChatInfo();

    CommonResult<String> arrange(ArrangeParam arrangeParam) throws DateBaseException;

    CommonResult<MessageTemplateResult> messageTemplate();

    CommonResult<MessageNumResult> messageNum(Integer status);

    CommonResult<String> send(SendParam sendParam) throws DateBaseException;

}

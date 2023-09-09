package com.sipc.mmtbackend.service.c;

import com.sipc.mmtbackend.pojo.c.param.MessageParam.ReadMessageParam;
import com.sipc.mmtbackend.pojo.c.param.MessageParam.SubmitStateParam;
import com.sipc.mmtbackend.pojo.c.result.MessageResult.GetMessageResult;
import com.sipc.mmtbackend.pojo.c.result.MessageResult.MessageResult;
import com.sipc.mmtbackend.pojo.dto.CommonResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface MessageService {
    CommonResult<MessageResult> getAllMessages(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse);

    CommonResult<GetMessageResult> getMessage(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Integer organizationId);

    CommonResult<String> readMessage(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, ReadMessageParam param);

    CommonResult<String> updateState(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, SubmitStateParam param);
}

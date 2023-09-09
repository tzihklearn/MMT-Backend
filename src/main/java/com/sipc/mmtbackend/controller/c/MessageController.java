package com.sipc.mmtbackend.controller.c;

import com.sipc.mmtbackend.pojo.c.param.MessageParam.ReadMessageParam;
import com.sipc.mmtbackend.pojo.c.param.MessageParam.SubmitStateParam;
import com.sipc.mmtbackend.pojo.c.result.MessageResult.GetMessageResult;
import com.sipc.mmtbackend.pojo.c.result.MessageResult.MessageResult;
import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.service.c.MessageService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 肖琰
 */
@RestController
@RequestMapping("/c/message")
public class MessageController {
    @Resource
    private MessageService messageService;

    @Resource
    private HttpServletRequest request;

    @Resource
    private HttpServletResponse response;

    /**
     * @apiNote 获取消息摘要 C-已认证用户
     */
    @GetMapping("/all")
    public CommonResult<MessageResult> getAllMessages() {
        return messageService.getAllMessages(request, response);
    }

    /**
     * @apiNote 获取指定组织的消息 C-已认证用户
     * @param organizationId 组织ID
     */
    @GetMapping("/org")
    public CommonResult<GetMessageResult> getMessages(
            @RequestParam(value = "organizationId") Integer organizationId
    ){
        return messageService.getMessage(request, response, organizationId);
    }

    /**
     * @apiNote 消息已读 C-已认证用户
     * @param param 消息ID
     */
    @PostMapping("/read")
    public CommonResult<String> readMessage(@Validated @RequestBody ReadMessageParam param){
        return messageService.readMessage(request, response, param);
    }

    /**
     * @apiNote 学生提交状反馈 C-已认证用户
     */
    @PostMapping("/state")
    public CommonResult<String> submitState(@Validated @RequestBody SubmitStateParam param) {
        return messageService.updateState(request, response, param);

    }
}

package com.sipc.mmtbackend.controller.c;

import com.sipc.mmtbackend.pojo.c.param.SuggestionParam.PhotoCodeParam;
import com.sipc.mmtbackend.pojo.c.param.SuggestionParam.PhotoParam;
import com.sipc.mmtbackend.pojo.c.param.SuggestionParam.SuggestionParam;
import com.sipc.mmtbackend.pojo.c.result.SuggestionResult.PhotoCode;
import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.service.c.SuggestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author DoudiNCer
 */
@RestController
@RequestMapping("/suggestion")
public class SuggestionController {
    @Autowired
    SuggestionService suggestionService;
    @Resource
    HttpServletRequest request;
    @Resource
    HttpServletResponse response;

    /**
     * 意见反馈图片上传 C-已认证用户
     * @param param 图片文件
     */
    @PutMapping("/upload-photo")
    public CommonResult<PhotoCode> uploadPhoto(@Validated @RequestBody PhotoParam param){
        return suggestionService.uploadPhoto(request, response, param);
    }

    /**
     * 删除已上传的图片 C-已认证用户
     * @param param 图片代码
     */
    @DeleteMapping("/delete-photo")
    public CommonResult<String> deletePhoto(@Validated @RequestBody PhotoCodeParam param){
        return suggestionService.deleteSuggestionPhoto(request, response, param);
    }

    /**
     * 提交反馈 C-已认证用户
     * @param param 意见反馈
     */
    CommonResult<String> submitSugestion(@Validated @RequestBody SuggestionParam param){
        return suggestionService.submitSuggestion(request, response, param);
    }
}

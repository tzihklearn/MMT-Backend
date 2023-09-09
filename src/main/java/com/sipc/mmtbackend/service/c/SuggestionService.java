package com.sipc.mmtbackend.service.c;

import com.sipc.mmtbackend.pojo.c.param.SuggestionParam.PhotoCodeParam;
import com.sipc.mmtbackend.pojo.c.param.SuggestionParam.PhotoParam;
import com.sipc.mmtbackend.pojo.c.param.SuggestionParam.SuggestionParam;
import com.sipc.mmtbackend.pojo.c.result.SuggestionResult.PhotoCode;
import com.sipc.mmtbackend.pojo.dto.CommonResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface SuggestionService {
    CommonResult<PhotoCode> uploadPhoto(HttpServletRequest request, HttpServletResponse response, PhotoParam param);

    CommonResult<String> deleteSuggestionPhoto(HttpServletRequest request, HttpServletResponse response, PhotoCodeParam param);

    CommonResult<String> submitSuggestion(HttpServletRequest request, HttpServletResponse response, SuggestionParam param);
}

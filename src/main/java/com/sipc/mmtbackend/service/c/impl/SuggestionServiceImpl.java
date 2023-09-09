package com.sipc.mmtbackend.service.c.impl;

import com.sipc.mmtbackend.controller.c.UpdateUserInfoController;
import com.sipc.mmtbackend.mapper.FeedbackMapper;
import com.sipc.mmtbackend.pojo.c.param.IsCertificationParam;
import com.sipc.mmtbackend.pojo.c.param.SuggestionParam.PhotoCodeParam;
import com.sipc.mmtbackend.pojo.c.param.SuggestionParam.PhotoParam;
import com.sipc.mmtbackend.pojo.c.param.SuggestionParam.SuggestionParam;
import com.sipc.mmtbackend.pojo.c.result.SuggestionResult.PhotoCode;
import com.sipc.mmtbackend.pojo.domain.Feedback;
import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.pojo.dto.resultEnum.ResultEnum;
import com.sipc.mmtbackend.service.c.SuggestionService;
import com.sipc.mmtbackend.utils.PictureUtil.PictureUtil;
import com.sipc.mmtbackend.utils.PictureUtil.pojo.PictureUsage;
import com.sipc.mmtbackend.utils.PictureUtil.pojo.UsageEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Slf4j
@Transactional
public class SuggestionServiceImpl implements SuggestionService {

    private final UpdateUserInfoController updateUserInfoController;
    private final FeedbackMapper feedbackMapper;
    private final PictureUtil pictureUtil;

    @Override
    public CommonResult<PhotoCode> uploadPhoto(HttpServletRequest request, HttpServletResponse response, PhotoParam param) {
        // 验证登录状态
        CommonResult<IsCertificationParam> isCertificationParamCommonResult = updateUserInfoController.updateUserInfo(request, response);
        if (!Objects.equals(isCertificationParamCommonResult.getCode(), ResultEnum.SUCCESS.getCode())) {
            return CommonResult.fail(isCertificationParamCommonResult.getCode(), isCertificationParamCommonResult.getMessage());
        }
        if (!isCertificationParamCommonResult.getData().getIs_certification())
            return CommonResult.fail("A0140", "用户未认证");

        String photoCode = pictureUtil.uploadPicture(
                param.getFile(), new PictureUsage(UsageEnum.C_USER_AVATAR, isCertificationParamCommonResult.getData().getUserId().toString()));
        if (photoCode == null)
            return CommonResult.serverError();
//        if (photoCode.equals("TOOBIG"))
//            return CommonResult.fail("A0703", "图片太大");
        PhotoCode result = new PhotoCode();
        result.setPhotoCode(photoCode);
        return CommonResult.success(result);
    }

    @Override
    public CommonResult<String> deleteSuggestionPhoto(HttpServletRequest request, HttpServletResponse response, PhotoCodeParam param) {
        // 验证登录状态
        CommonResult<IsCertificationParam> isCertificationParamCommonResult = updateUserInfoController.updateUserInfo(request, response);
        if (!Objects.equals(isCertificationParamCommonResult.getCode(), ResultEnum.SUCCESS.getCode())) {
            return CommonResult.fail(isCertificationParamCommonResult.getCode(), isCertificationParamCommonResult.getMessage());
        }
        if (!isCertificationParamCommonResult.getData().getIs_certification())
            return CommonResult.fail("A0140", "用户未认证");

        Boolean rmResult = pictureUtil.dropPicture(param.getPhotoCode());

        if (rmResult == null)
            return CommonResult.serverError();
        if (!rmResult)
            return CommonResult.fail("图片不存在");
        return CommonResult.success();
    }

    @Override
    public CommonResult<String> submitSuggestion(HttpServletRequest request, HttpServletResponse response, SuggestionParam param) {
        // 验证登录状态
        CommonResult<IsCertificationParam> isCertificationParamCommonResult = updateUserInfoController.updateUserInfo(request, response);
        if (!Objects.equals(isCertificationParamCommonResult.getCode(), ResultEnum.SUCCESS.getCode())) {
            return CommonResult.fail(isCertificationParamCommonResult.getCode(), isCertificationParamCommonResult.getMessage());
        }
        if (!isCertificationParamCommonResult.getData().getIs_certification())
            return CommonResult.fail("A0140", "用户未认证");

        List<String> photoCodes = param.getPhotoCode();
        if (photoCodes != null && photoCodes.size() > 3)
            return CommonResult.fail("A0604", "图片数量太多");

        // 处理反馈建议
        Feedback feedback = new Feedback();
        feedback.setFeedback(param.getDiscribe() + param.getPhotoCode());
        feedback.setUserId(isCertificationParamCommonResult.getData().getUserId());
        feedback.setIsB(false);
        feedback.setDate(LocalDateTime.now());
        feedback.setName(isCertificationParamCommonResult.getData().getName());
        feedback.setEmail(param.getPhone() + (param.getCall() == 0 ? "Yes" : "No"));
        int insert = feedbackMapper.insert(feedback);
        if (insert != 1){
            log.info("C 端用户 " + isCertificationParamCommonResult.getData().getUserId() + "提交的建议 " + feedback + "写入失败");
            return CommonResult.serverError();
        }
        // 获取建议ID
//        Integer id = data.getId();
//        if (photoCodes != null) {
//            // 图片入库
//            for (String photoCode : photoCodes) {
//                String url = PhotoUtils.uploadPhoto(photoCode);
//                if (url == null)
//                    continue;
//                Picture picture = new Picture();
//                picture.setSuggestionId(id);
//                picture.setUploadUrl(url);
//                pictureMapper.insertPicture(picture);
//            }
//        }
        return CommonResult.success();
    }
}

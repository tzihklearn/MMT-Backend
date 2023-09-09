package com.sipc.mmtbackend.utils.checkRoleUtils.param;

import com.sipc.mmtbackend.pojo.dto.resultEnum.ResultEnum;
import lombok.Data;

@Data
public class CheckResultParam {
    /**
     * 是否有权限操作
     */
    private boolean result;

    /**
     * 如果result为true，返回用户信息，c端返回openid，b端返回studentId
     */
    private String data;

    /**
     * 如果result为false，错误码
     */
    private String errcode;

    /**
     * 如果result为false，错误信息
     */
    private String errmsg;

    public static CheckResultParam fail(String code, String message) {
        CheckResultParam checkResultParam = new CheckResultParam();
        checkResultParam.setResult(false);
        checkResultParam.setErrmsg(message);
        checkResultParam.setErrcode(code);

        return checkResultParam;
    }

    public static CheckResultParam success(String data) {
        CheckResultParam checkResultParam = new CheckResultParam();
        checkResultParam.setData(data);
        checkResultParam.setResult(true);

        return checkResultParam;
    }

    public static CheckResultParam fail(ResultEnum resultEnum) {
        CheckResultParam checkResultParam = new CheckResultParam();
        checkResultParam.setResult(false);
        checkResultParam.setErrmsg(resultEnum.getMessage());
        checkResultParam.setErrcode(resultEnum.getCode());

        return checkResultParam;
    }
}

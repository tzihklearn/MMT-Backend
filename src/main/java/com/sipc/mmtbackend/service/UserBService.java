package com.sipc.mmtbackend.service;

import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.pojo.dto.param.UserBParam.LoginPassParam;
import com.sipc.mmtbackend.pojo.dto.param.UserBParam.RegParam;
import com.sipc.mmtbackend.pojo.dto.result.UserBResult.LoginResult;
import com.sipc.mmtbackend.pojo.exceptions.DateBaseException;

public interface UserBService {
    CommonResult<String> registUser(RegParam param) throws DateBaseException;

    CommonResult<LoginResult> loginByPass(LoginPassParam param);
}

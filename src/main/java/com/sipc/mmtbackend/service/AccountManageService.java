package com.sipc.mmtbackend.service;

import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.pojo.dto.param.superAdmin.ReviseMemberInfoParam;
import com.sipc.mmtbackend.pojo.dto.result.superAdmin.ICodeResult;
import com.sipc.mmtbackend.pojo.dto.result.superAdmin.MemberInfoResult;
import com.sipc.mmtbackend.pojo.exceptions.DateBaseException;

public interface AccountManageService {

    CommonResult<ICodeResult> generatedICode(Integer organizationId);

    CommonResult<MemberInfoResult> allMemberInfo(Integer organizationId, Integer pageNum);

    CommonResult<MemberInfoResult> siftMemberInfo(Integer organizationId, Integer pageNum, Integer sort, String permission);

    CommonResult<String> reviseMemberInfo(ReviseMemberInfoParam reviseMemberInfoParam) throws DateBaseException;

}

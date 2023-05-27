package com.sipc.mmtbackend.service;

import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.pojo.dto.param.superAdmin.DeleteMemberParam;
import com.sipc.mmtbackend.pojo.dto.param.superAdmin.ReviseMemberInfoParam;
import com.sipc.mmtbackend.pojo.dto.param.superAdmin.ReviseMemberPasswdParam;
import com.sipc.mmtbackend.pojo.dto.result.superAdmin.ICodeResult;
import com.sipc.mmtbackend.pojo.dto.result.superAdmin.MemberInfoResult;
import com.sipc.mmtbackend.pojo.exceptions.DateBaseException;

public interface AccountManageService {

    CommonResult<ICodeResult> generatedICode();

    CommonResult<MemberInfoResult> allMemberInfo(Integer pageNum);

    CommonResult<MemberInfoResult> siftMemberInfo(Integer pageNum, Integer sort, String permission);

    CommonResult<String> reviseMemberInfo(ReviseMemberInfoParam reviseMemberInfoParam) throws DateBaseException;

    CommonResult<String> reviseMemberPasswd(ReviseMemberPasswdParam reviseMemberPasswdParam) throws DateBaseException;

    CommonResult<String> deleteMember(DeleteMemberParam deleteMemberParam) throws DateBaseException;

}

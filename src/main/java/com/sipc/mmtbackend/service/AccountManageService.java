package com.sipc.mmtbackend.service;

import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.pojo.dto.param.superAdmin.*;
import com.sipc.mmtbackend.pojo.dto.result.superAdmin.ICodeResult;
import com.sipc.mmtbackend.pojo.dto.result.superAdmin.MemberInfoResult;
import com.sipc.mmtbackend.pojo.exceptions.DateBaseException;
import com.sipc.mmtbackend.pojo.exceptions.ValidateException;

public interface AccountManageService {

    CommonResult<ICodeResult> generatedICode();

    CommonResult<MemberInfoResult> allMemberInfo(Integer pageNum);

    CommonResult<MemberInfoResult> siftMemberInfo(Integer pageNum, Integer sort, String permission);

    CommonResult<String> reviseMemberInfo(ReviseMemberInfoParam reviseMemberInfoParam) throws DateBaseException, ValidateException;

    CommonResult<String> reviseMemberPasswd(ReviseMemberPasswdParam reviseMemberPasswdParam) throws DateBaseException;

    CommonResult<String> reviseMember(ReviseMemberParam reviseMemberParam) throws DateBaseException, ValidateException;

    CommonResult<String> addMember(AddMemberParam addMemberParam) throws ValidateException, DateBaseException;

    CommonResult<String> deleteMember(DeleteMemberParam deleteMemberParam) throws DateBaseException;

}

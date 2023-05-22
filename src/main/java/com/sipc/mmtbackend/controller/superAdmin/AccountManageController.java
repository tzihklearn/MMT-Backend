package com.sipc.mmtbackend.controller.superAdmin;

import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.pojo.dto.param.superAdmin.ReviseMemberInfoParam;
import com.sipc.mmtbackend.pojo.dto.result.superAdmin.ICodeResult;
import com.sipc.mmtbackend.pojo.dto.result.superAdmin.MemberInfoResult;
import com.sipc.mmtbackend.pojo.exceptions.DateBaseException;
import com.sipc.mmtbackend.service.AccountManageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 超级管理相关操作接口的控制层类
 * @author tzih
 * @version v1.0
 * @since 2023.05.20
 */
@RestController
@RequestMapping("/b/admin/account")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class AccountManageController {

    /**
     * 超级管理服务层接口
     */
    private final AccountManageService accountManageService;

    /**
     * 生成社团邀请码，时限10min
     * @param organizationId 社团组织id
     * @return 返回处理的结果，包含生成的社团邀请码
     * @see ICodeResult
     */
    @PostMapping("/icode/generated")
    public CommonResult<ICodeResult> generatedICode(@RequestParam Integer organizationId) {
        return accountManageService.generatedICode(organizationId);
    }

    /**
     * 获取社团成员列表接口
     * @param organizationId 社团组织id
     * @param pageNum 当前页数
     * @return 返回处理的结果，包含社团成员列表
     * @see MemberInfoResult
     */
    @GetMapping("/members/info")
    public CommonResult<MemberInfoResult> allMemberInfo(@RequestParam Integer organizationId, @RequestParam Integer pageNum) {
        return accountManageService.allMemberInfo(organizationId, pageNum);
    }

    /**
     * 筛选社团成员列表接口
     * @param organizationId 社团组织id
     * @param pageNum 当前页数
     * @param sort 学号排序， 0为正序， 1为倒序（默认为0）
     * @param permission 成员权限筛选项
     * @return 返回处理的结果，包含社团成员列表
     * @see MemberInfoResult
     */
    @GetMapping("/members/info/sift")
    public CommonResult<MemberInfoResult> siftMemberInfo(@RequestParam Integer organizationId,
                                                         @RequestParam Integer pageNum,
                                                         @RequestParam(required = false, defaultValue = "0") Integer sort,
                                                         @RequestParam(required = false) String permission) {

        return accountManageService.siftMemberInfo(organizationId, pageNum, sort, permission);
    }

    /**
     * 修改社团成员信息和权限接口
     * @param reviseMemberInfoParam 修改社团成员信息和权限的参数实体类
     * @return 返回处理的结果
     * @throws DateBaseException 自定义的数据库操作异常，抛出用于统一异常处理
     * @see ReviseMemberInfoParam
     */
    @PostMapping("/members/revise/info")
    public CommonResult<String> reviseMemberInfo(@RequestBody ReviseMemberInfoParam reviseMemberInfoParam) throws DateBaseException {
        return accountManageService.reviseMemberInfo(reviseMemberInfoParam);
    }



}

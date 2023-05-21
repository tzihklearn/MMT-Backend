package com.sipc.mmtbackend.controller.superAdmin;

import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.pojo.dto.result.superAdmin.ICodeResult;
import com.sipc.mmtbackend.pojo.dto.result.superAdmin.MemberInfoResult;
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


}

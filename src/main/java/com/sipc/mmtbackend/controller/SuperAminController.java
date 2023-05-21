package com.sipc.mmtbackend.controller;

import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.pojo.dto.result.ICodeResult;
import com.sipc.mmtbackend.service.SuperAminService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 超级管理相关操作接口的控制层类
 * @author tzih
 * @version v1.0
 * @since 2023.05.20
 */
@RestController
@RequestMapping("/b/admin")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class SuperAminController {

    /**
     * 超级管理服务层接口
     */
    private final SuperAminService superAminService;

    /**
     * 生成社团邀请码，时限10min
     * @param organizationId 社团组织id
     * @return 返回处理的结果，包含生成的社团邀请码
     * @see ICodeResult
     */
    @PostMapping("/icode/generated")
    public CommonResult<ICodeResult> generatedICode(Integer organizationId) {
        return superAminService.generatedICode(organizationId);
    }

}

package com.sipc.mmtbackend.service.impl;

import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.pojo.dto.result.ICodeResult;
import com.sipc.mmtbackend.service.SuperAminService;
import com.sipc.mmtbackend.utils.ICodeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 超级管理接口的实现类
 * @author tzih
 * @version v1.0
 * @since 2023.05.20
 */
@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class SuperAminServiceImpl implements SuperAminService {

    /**
     * 社团邀请码工具类
     */
    private final ICodeUtil iCodeUtil;

    /**
     * 生成社团邀请码，并将其放入redis中，时限10min
     * @param organizationId 社团组织id
     * @return 生成社团邀请码的返回类，包含生成的社团邀请码
     * @see ICodeResult
     */
    @Override
    public CommonResult<ICodeResult> generatedICode(Integer organizationId) {

        /*
          生成社团邀请澳门，并将其放入redis中，时限10min
         */
        String ICode = iCodeUtil.setICodeRedis(organizationId, 6);

        /*
          判断是否成功生成社团邀码
         */
        if (ICode != null) {
            ICodeResult iCodeResult = new ICodeResult();

            iCodeResult.setICode(ICode);

            return CommonResult.success(iCodeResult);
        }

        return CommonResult.fail("生成社团邀请码失败失败");
    }
}

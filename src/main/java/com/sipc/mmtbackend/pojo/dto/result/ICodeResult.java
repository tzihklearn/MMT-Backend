package com.sipc.mmtbackend.pojo.dto.result;

import lombok.Data;

/**
 * 生成社团邀请码接口的返回结果类
 * @author tzih
 * @version v1.0
 * @since 2023.05.20
 */
@Data
public class ICodeResult {

    /**
     * 社团邀请码
     */
    private String ICode;

}

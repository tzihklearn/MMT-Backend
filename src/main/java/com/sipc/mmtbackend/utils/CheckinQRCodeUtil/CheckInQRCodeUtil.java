package com.sipc.mmtbackend.utils.CheckinQRCodeUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 签到二维码相关工具类
 *
 * @author DoudiNCer
 */
@Component
@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class CheckInQRCodeUtil {

    /**
     * 获取某个组织当天的签到二维码
     *
     * @param organizationId 组织ID
     * @return 二维码图片的 Base64 编码，出现异常返回 NULL
     */
    public String getCheckinQRCode(int organizationId){
        return "";
    }
}

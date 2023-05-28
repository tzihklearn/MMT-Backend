package com.sipc.mmtbackend.utils.PictureUtil.pojo;

import lombok.Getter;
import lombok.ToString;

/**
 * 图片用途与代码
 *
 * @author DoudiNCer
 */
@Getter
@ToString
public enum UsageEnum {
    // B 端用户默认头像
    B_USER_AVATAR(0, "BUserAvatar"),
    // 组织默认头像
    ORG_AVATAR(1, "OrganizationAvatar"),
    // C 端用户默认头像
    C_USER_AVATAR(2, "CUserAvatar");
    private final Integer id;
    private final String usage;

    UsageEnum(Integer id, String usage) {
        this.id = id;
        this.usage = usage;
    }
}

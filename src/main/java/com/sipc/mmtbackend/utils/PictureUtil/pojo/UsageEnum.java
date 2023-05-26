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
    B_USER_AVATAR(0, "BUserAvatar"),
    ORG_AVATAR(1, "OrganizationAvatar");
    private final Integer id;
    private final String usage;

    UsageEnum(Integer id, String usage) {
        this.id = id;
        this.usage = usage;
    }
}

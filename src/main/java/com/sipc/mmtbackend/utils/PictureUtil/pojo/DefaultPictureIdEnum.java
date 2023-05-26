package com.sipc.mmtbackend.utils.PictureUtil.pojo;

import lombok.Getter;
import lombok.ToString;

/**
 * 各种默认头像的 PicrtureId
 *
 * @author DoudiNCer
 */
@Getter
@ToString
public enum DefaultPictureIdEnum {
    // B 端用户默认头像
    B_USER_AVATAR("Head"),
    // 组织默认头像
    ORG_AVATAR("OAva"),
    // C 端用户默认头像
    C_USER_AVATAR("CHead");

    private final String pictureId;

    DefaultPictureIdEnum(String picId) {
        this.pictureId = picId;
    }
}

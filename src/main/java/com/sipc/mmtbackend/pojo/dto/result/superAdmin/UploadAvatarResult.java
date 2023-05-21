package com.sipc.mmtbackend.pojo.dto.result.superAdmin;

import lombok.Data;

/**
 * 上传社团头像接口的返回体，返回相应的社团头像链接
 * @author tzih
 * @version v1.0
 * @since 2023.05.04
 */
@Data
public class UploadAvatarResult {

    /**
     * 社团头像链接
     */
    private String avatarUrl;
}

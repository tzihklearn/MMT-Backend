package com.sipc.mmtbackend.utils.PictureUtil.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 用于溯源的图片信息
 *
 * @author DoudiNCer
 */
@Data
@AllArgsConstructor
public class PictureUsage {
    private UsageEnum usage;
    private String uploaderToken;
}

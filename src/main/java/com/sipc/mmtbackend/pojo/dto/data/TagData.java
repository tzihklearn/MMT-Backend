package com.sipc.mmtbackend.pojo.dto.data;

import lombok.Data;

/**
 * 社团宣传信息标签，分为系统标签和自定义标签
 * @author tzih
 * @version v1.0
 * @since 2023.04.23
 */
@Data
public class TagData {

    /**
     * 社团宣传信息标签
     */
    private String tag;

    /**
     * 标签类型，1为系统标签，2为自定义标签
     */
    private Integer type;
}

package com.sipc.mmtbackend.pojo.dto.param;

import lombok.Data;

/**
 * @author tzih
 * @version v1.0
 * @since 2023.04.23
 */
@Data
public class TagData {

    private String tag;

    /**
     * 标签类型，1为系统标签，2为自定义标签
     */
    private Integer type;
}

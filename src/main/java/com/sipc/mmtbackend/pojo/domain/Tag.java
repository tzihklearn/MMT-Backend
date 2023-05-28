package com.sipc.mmtbackend.pojo.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author DoudiNCer
 * @since 2023-05-06
 */
@Getter
@Setter
@TableName("tag")
public class Tag implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("name")
    private String name;

    /**
     * 1 系统标签 2自定义标签
     */
    @TableField("type")
    private Byte type;

    @TableField("is_deleted")
    @TableLogic
    private Byte isDeleted;
}

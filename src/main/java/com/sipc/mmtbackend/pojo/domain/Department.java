package com.sipc.mmtbackend.pojo.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author tzih
 * @since 2023-04-28
 */
@Getter
@Setter
@TableName("department")
public class Department implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("name")
    private String name;

    @TableField("brief_description")
    private String briefDescription;

    @TableField("description")
    private String description;

    @TableField(value = "standard", updateStrategy = FieldStrategy.IGNORED)
    private String standard;

    @TableField("is_deleted")
    @TableLogic
    private Byte isDeleted;
}

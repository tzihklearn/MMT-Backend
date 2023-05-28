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
@TableName("department")
public class Department implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 组织ID
     */
    @TableField("organization_id")
    private Integer organizationId;

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

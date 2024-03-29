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
 * @author tzih
 * @since 2023-05-03
 */
@Getter
@Setter
@TableName("organization_tag_merge")
public class OrganizationTagMerge implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("organization_id")
    private Integer organizationId;

    @TableField("tag_id")
    private Integer tagId;

    /**
     * 标签的类型，冗余字段，便于查询
     */
    @TableField("tag_type")
    private Byte tagType;

    @TableField("is_deleted")
    @TableLogic
    private Byte isDeleted;
}

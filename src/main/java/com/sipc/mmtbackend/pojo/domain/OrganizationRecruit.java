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
@TableName("organization_recruit")
public class OrganizationRecruit implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "organization_id", type = IdType.AUTO)
    private Integer organizationId;

    @TableField(value = "description", updateStrategy = FieldStrategy.IGNORED)
    private String description;

    @TableField(value = "feature", updateStrategy = FieldStrategy.IGNORED)
    private String feature;

    @TableField(value = "daily", updateStrategy = FieldStrategy.IGNORED)
    private String daily;

    @TableField(value = "slogan", updateStrategy = FieldStrategy.IGNORED)
    private String slogan;

    @TableField(value = "contact_info", updateStrategy = FieldStrategy.IGNORED)
    private String contactInfo;

    @TableField(value = "more", updateStrategy = FieldStrategy.IGNORED)
    private String more;

    @TableField("is_deleted")
    @TableLogic
    private Byte isDeleted;
}

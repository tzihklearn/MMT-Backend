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
@TableName("organization_recruit")
public class OrganizationRecruit implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "organization_id", type = IdType.AUTO)
    private Integer organizationId;

    @TableField("description")
    private String description;

    @TableField("feature")
    private String feature;

    @TableField("daily")
    private String daily;

    @TableField("slogan")
    private String slogan;

    @TableField("contact_info")
    private String contactInfo;

    @TableField("more")
    private String more;

    @TableField("is_deleted")
    @TableLogic
    private Byte isDeleted;
}

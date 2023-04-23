package com.sipc.mmtbackend.pojo.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author tzih
 * @since 2023-04-23
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
}

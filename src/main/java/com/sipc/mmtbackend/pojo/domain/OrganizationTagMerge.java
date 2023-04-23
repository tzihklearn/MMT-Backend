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
@TableName("organization_tag_merge")
public class OrganizationTagMerge implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "organization_id", type = IdType.AUTO)
    private Integer organizationId;

    @TableId(value = "tag_id", type = IdType.AUTO)
    private Integer tagId;

    /**
     * 标签的类型，冗余字段，便于查询
     */
    @TableField("tag_type")
    private Byte tagType;
}

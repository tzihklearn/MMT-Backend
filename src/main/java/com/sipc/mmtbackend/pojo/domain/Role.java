package com.sipc.mmtbackend.pojo.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

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
@ToString
@TableName("role")
public class Role implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 角色ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 组织ID
     */
    @TableField("organization_id")
    private Integer organizationId;

    /**
     * 权限ID
     */
    @TableField("permission_id")
    private Integer permissionId;

    /**
     * 是否安全
     */
    @TableField("is_deleted")
    @TableLogic
    private Boolean isDeleted;
}

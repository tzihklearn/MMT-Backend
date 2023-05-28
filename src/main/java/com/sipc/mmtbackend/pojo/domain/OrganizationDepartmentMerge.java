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
@TableName("organization_department_merge")
public class OrganizationDepartmentMerge implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("organization_id")
    private Integer organizationId;

    @TableField("department_id")
    private Integer departmentId;

    @TableField("is_deleted")
    @TableLogic
    private Byte isDeleted;
}

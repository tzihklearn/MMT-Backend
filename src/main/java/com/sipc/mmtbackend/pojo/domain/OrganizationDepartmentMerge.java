package com.sipc.mmtbackend.pojo.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
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
 * @since 2023-04-28
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

package com.sipc.mmtbackend.pojo.domain;

import com.baomidou.mybatisplus.annotation.IdType;
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
 * @since 2023-04-24
 */
@Getter
@Setter
@TableName("organization_department_merge")
public class OrganizationDepartmentMerge implements Serializable {

    private static final long serialVersionUID = 1L;

//    @TableId(value = "organization_id", type = IdType.AUTO)
    private Integer organizationId;

//    @TableId(value = "department_id", type = IdType.AUTO)
    private Integer departmentId;
}

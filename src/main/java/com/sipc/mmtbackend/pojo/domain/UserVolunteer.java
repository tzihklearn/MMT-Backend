package com.sipc.mmtbackend.pojo.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author tzih
 * @since 2023-08-23
 */
@Getter
@Setter
@TableName("user_volunteer")
public class UserVolunteer implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("user_id")
    private Integer userId;

    @TableField("admission_id")
    private Integer admissionId;

    @TableField("department_id")
    private Integer departmentId;

    @TableField("organization_order")
    private Integer organizationOrder;

    @TableField("department_order")
    private Integer departmentOrder;

    @TableField("register_time")
    private LocalDateTime registerTime;

    @TableField("is_transfers")
    private Byte isTransfers;

    @TableField("is_deleted")
    @TableLogic
    private Byte isDeleted;
}

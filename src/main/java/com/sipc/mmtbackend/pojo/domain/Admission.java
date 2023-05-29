package com.sipc.mmtbackend.pojo.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author DoudiNCer
 * @since 2023-05-29
 */
@Getter
@Setter
@TableName("admission")
public class Admission implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 发起者
     */
    @TableField("initiator")
    private Integer initiator;

    /**
     * 组织ID
     */
    @TableField("organization_id")
    private Integer organizationId;

    @TableField("start_time")
    private LocalDateTime startTime;

    @TableField("end_time")
    private LocalDateTime endTime;

    /**
     * 允许报名部门数
     */
    @TableField("allow_department_amount")
    private Integer allowDepartmentAmount;

    /**
     * 计划总面试轮次
     */
    @TableField("rounds")
    private Integer rounds;

    @TableField("is_deleted")
    @TableLogic
    private Byte isDeleted;
}

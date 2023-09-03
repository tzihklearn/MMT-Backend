package com.sipc.mmtbackend.pojo.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;

/**
 * <p>
 * 
 * </p>
 *
 * @author tzih
 * @since 2023-06-03
 */
@Data
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

    /**
     * 为空时代表还没有发布纳新
     */
    @TableField("start_time")
    private LocalDateTime startTime;

    @TableField("end_time")
    private LocalDateTime endTime;

    @TableField(value = "department_num", updateStrategy = FieldStrategy.IGNORED)
    private Integer departmentNum;

    /**
     * 允许报名部门数
     */
    @TableField(value = "allow_department_amount", updateStrategy = FieldStrategy.IGNORED)
    private Integer allowDepartmentAmount;

    /**
     * 计划总面试轮次
     */
    @TableField(value = "rounds", updateStrategy = FieldStrategy.IGNORED)
    private Integer rounds;

    /**
     * 是否允许调剂，0不允许，1允许
     */
    @TableField(value = "is_transfers", updateStrategy = FieldStrategy.IGNORED)
    private Byte isTransfers;

    @TableField("is_deleted")
    @TableLogic
    private Byte isDeleted;
}

package com.sipc.mmtbackend.pojo.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;

/**
 * <p>
 * 
 * </p>
 *
 * @author tzih
 * @since 2023-08-15
 */
@Data
@TableName("interview_status")
public class InterviewStatus implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 面试者id
     */
    @TableField("user_id")
    private Integer userId;

    /**
     * 面试地点id
     */
    @TableField("admission_address_id")
    private Integer admissionAddressId;

    /**
     * 开始时间
     */
    @TableField("start_time")
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @TableField("end_time")
    private LocalDateTime endTime;

    @TableField("state")
    private Integer state;

    /**
     * 签到时间
     */
    @TableField("sign_in_time")
    private LocalDateTime signInTime;

    /**
     * 真正结束时间
     */
    @TableField("true_end_time")
    private LocalDateTime trueEndTime;

    /**
     * 社团志愿次序
     */
    @TableField("organization_order")
    private Integer organizationOrder;

    /**
     * 部门志愿次序
     */
    @TableField("department_order")
    private Integer departmentOrder;

    /**
     * 面试社团id,冗余字段
     */
    @TableField("admission_id")
    private Integer admissionId;

    /**
     * 面试部门id,冗余字段
     */
    @TableField("department_id")
    private Integer departmentId;

    /**
     * 面试轮次。冗余字段
     */
    @TableField("round")
    private Integer round;

    @TableField("is_message")
    private Integer isMessage;

    /**
     * 是否已经调剂
     */
    @TableField("is_transfers")
    private Byte isTransfers;

    /**
     * 逻辑删除字段
     */
    @TableField("is_deleted")
    @TableLogic
    private Byte isDeleted;
}

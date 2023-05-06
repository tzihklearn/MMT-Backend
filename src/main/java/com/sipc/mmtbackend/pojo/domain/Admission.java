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
 * @author DoudiNCer
 * @since 2023-05-06
 */
@Getter
@Setter
@TableName("admission")
public class Admission implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("initiator")
    private Integer initiator;

    @TableField("organization_id")
    private Integer organizationId;

    @TableField("start_time")
    private LocalDateTime startTime;

    @TableField("end_time")
    private LocalDateTime endTime;

    /**
     * 允许纳新部门数
     */
    @TableField("allow_department_amount")
    private Integer allowDepartmentAmount;

    /**
     * 面试轮次
     */
    @TableField("rounds")
    private Integer rounds;

    @TableField("is_deleted")
    @TableLogic
    private Byte isDeleted;
}

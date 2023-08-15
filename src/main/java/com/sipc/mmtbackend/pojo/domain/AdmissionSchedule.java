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
 * @since 2023-08-13
 */
@Getter
@Setter
@TableName("admission_schedule")
public class AdmissionSchedule implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 社团纳新部门id
     */
    @TableField("admission_department_id")
    private Integer admissionDepartmentId;

    /**
     * 纳新轮次
     */
    @TableField("round")
    private Integer round;

    /**
     * 逻辑删除字段
     */
    @TableField("is_deleted")
    @TableLogic
    private Byte isDeleted;
}

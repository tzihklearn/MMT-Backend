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
 * @since 2023-06-03
 */
@Getter
@Setter
@TableName("admission_question")
public class AdmissionQuestion implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 纳新id
     */
    @TableField("admission_id")
    private Integer admissionId;

    /**
     * 部门id
     */
    @TableField("department_id")
    private Integer departmentId;

    /**
     * 问题id
     */
    @TableField("question_id")
    private Integer questionId;

    /**
     * 问题类型， 1基本问题，2部门问题，3自定义问题
     */
    @TableField("question_type")
    private Integer questionType;

    /**
     * 问题次序
     */
    @TableField("`order`")
    private Integer order;

    /**
     * 逻辑删除字段
     */
    @TableField("is_deleted")
    @TableLogic
    private Byte isDeleted;
}

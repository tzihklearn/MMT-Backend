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
 * @since 2023-08-17
 */
@Getter
@Setter
@TableName("interview_question")
public class InterviewQuestion implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("admission_id")
    private Integer admissionId;

    @TableField("question_id")
    private Integer questionId;

    /**
     * 1面试基本评价，2面试综合评价，3面试问题
     */
    @TableField("question_type")
    private Integer questionType;

    @TableField("round")
    private Integer round;

    @TableField("order")
    private Integer order;

    @TableField("is_deleted")
    @TableLogic
    private Byte isDeleted;
}

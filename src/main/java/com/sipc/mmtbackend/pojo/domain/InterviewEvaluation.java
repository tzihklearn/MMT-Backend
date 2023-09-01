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
 * @author DoudiNCer
 * @since 2023-09-01
 */
@Getter
@Setter
@TableName("interview_evaluation")
public class InterviewEvaluation implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("interview_status_id")
    private Integer interviewStatusId;

    @TableField("user_b_id")
    private Integer userBId;

    @TableField("real_name")
    private Byte realName;

    /**
     * 1 通过，2失败，3待定
     */
    @TableField("is_pass")
    private Integer isPass;

    @TableField("pass_department_id")
    private Integer passDepartmentId;

    @TableField("evaluation")
    private String evaluation;

    @TableField("is_deleted")
    @TableLogic
    private Byte isDeleted;
}

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
 * @since 2023-08-23
 */
@Getter
@Setter
@TableName("question_score")
public class QuestionScore implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("interview_status_id")
    private Integer interviewStatusId;

    @TableField("user_b_id")
    private Integer userBId;

    @TableField("interview_question_id")
    private Integer interviewQuestionId;

    @TableField("score")
    private Integer score;

    @TableField("is_deleted")
    @TableLogic
    private Byte isDeleted;
}

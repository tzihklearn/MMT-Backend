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
 * @since 2023-05-06
 */
@Getter
@Setter
@TableName("admission_question_merge")
public class AdmissionQuestionMerge implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("admission_id")
    private Integer admissionId;

    @TableField("quesstion_id")
    private Integer quesstionId;

    /**
     * 1 系统问题，0 自定义问题，冗余字段
     */
    @TableField("type")
    private Byte type;

    @TableField("is_deleted")
    @TableLogic
    private Byte isDeleted;
}

package com.sipc.mmtbackend.pojo.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author tzih
 * @since 2023-05-03
 */
@Getter
@Setter
@TableName("question")
public class Question implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableField("id")
    private Integer id;

    @TableField("content")
    private String content;

    /**
     * 1 系统问题，0 自定义问题
     */
    @TableField("type")
    private Byte type;

    @TableField("organization_id")
    private Integer organizationId;

    @TableField("department_id")
    private Integer departmentId;

    /**
     * 0 判断题， 1  单项选择题
     */
    @TableField("is_select")
    private Integer isSelect;

    @TableField("option_A")
    private String optionA;

    @TableField("option_B")
    private String optionB;

    @TableField("option_C")
    private String optionC;

    @TableField("option_D")
    private String optionD;

    @TableField("is_deleted")
    @TableLogic
    private Byte isDeleted;
}

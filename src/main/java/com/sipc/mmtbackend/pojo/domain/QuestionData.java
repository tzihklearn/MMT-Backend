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
@TableName("question_data")
public class QuestionData implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 类型，1为系统内置问题，0为自定义内型
     */
    @TableField("type")
    private Integer type;

    /**
     * 选择类型，1单选，2多选，3下拉框，4输入框，5级联选择器，6量表题
     */
    @TableField("selectTypeId")
    private Integer selectTypeId;

    /**
     * 问题名称
     */
    @TableField("question")
    private String question;

    /**
     * 问题可选择的值，输入框则为提示
     */
    @TableField("value")
    private String value;

    /**
     * 可选择的条目，级联选择器为一共有几级
     */
    @TableField("num")
    private Integer num;

    /**
     * 面试问题的答案或提示
     */
    @TableField("answer")
    private String answer;

    /**
     * 逻辑删除字段
     */
    @TableField("is_deleted")
    @TableLogic
    private Byte isDeleted;
}

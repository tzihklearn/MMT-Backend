package com.sipc.mmtbackend.pojo.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * <p>
 * 
 * </p>
 *
 * @author DoudiNCer
 * @since 2023-05-25
 */
@Getter
@Setter
@ToString
@TableName("user_b")
public class UserB implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * B端用户ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * B端用户手机号（唯一）
     */
    @TableField("phone")
    private String phone;

    /**
     * B端学号（唯一）
     */
    @TableField("student_id")
    private String studentId;

    /**
     * 用户名
     */
    @TableField("user_name")
    private String userName;

    /**
     * 用户邮箱
     */
    @TableField("email")
    private String email;

    /**
     * 用户头像ID
     */
    @TableField("avatar_id")
    private String avatarId;

    /**
     * 是否安全
     */
    @TableField("is_deleted")
    @TableLogic
    private Boolean isDeleted;
}

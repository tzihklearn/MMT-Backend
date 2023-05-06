package com.sipc.mmtbackend.pojo.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
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
 * @since 2023-04-27
 */
@Getter
@Setter
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
     * B端密码
     */
    @TableField("password")
    private String password;

    /**
     * B端学号
     */
    @TableField("student_id")
    private String studentId;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;

    /**
     * 是否安全
     */
    @TableField("is_deleted")
    private Boolean isDeleted;
}

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
@TableName("user_info")
public class UserInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("student_id")
    private String studentId;

    @TableField("major_class_id")
    private Integer majorClassId;

    @TableField("name")
    private String name;

    /**
     * 1为男，0为女
     */
    @TableField("gander")
    private Byte gander;

    @TableField("phone")
    private String phone;

    @TableField("qq")
    private String qq;

    @TableField("email")
    private String email;

    @TableField("is_deleted")
    @TableLogic
    private Byte isDeleted;
}

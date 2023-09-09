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

    @TableField("wechat")
    private String wechat;

    @TableField("birthday")
    private String birthday;

    @TableField("weight")
    private Double weight;

    @TableField("height")
    private Double height;

    @TableField("is_certification")
    private Boolean isCertification;

    @TableField("tag")
    private Boolean tag;

    @TableField("aca_major_id")
    private Integer acaMajorId;

    @TableField("class_num")
    private String classNum;

    @TableField("avatar_url")
    private String avatarUrl;

    @TableField("nickname")
    private String nickname;

    @TableField("is_deleted")
    @TableLogic
    private Byte isDeleted;
}

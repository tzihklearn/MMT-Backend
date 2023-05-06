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
@TableName("organization")
public class Organization implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 组织ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 组织名称
     */
    @TableField("name")
    private String name;

    /**
     * 组织介绍
     */
    @TableField("description")
    private String description;

    /**
     * 组织头像ID（不含链接与扩展名）
     */
    @TableField("avatar_id")
    private String avatarId;

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

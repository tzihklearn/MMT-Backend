package com.sipc.mmtbackend.pojo.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author DoudiNCer
 * @since 2023-05-29
 */
@Getter
@Setter
@TableName("picture")
public class Picture implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 图片自增ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 图片在 Minio 的唯一ID
     */
    @TableField("pic_id")
    private String picId;

    /**
     * 图片用途
     */
    @TableField("pic_usage")
    private String picUsage;

    /**
     * 是否删除
     */
    @TableField("is_deleted")
    @TableLogic
    private Boolean isDeleted;
}

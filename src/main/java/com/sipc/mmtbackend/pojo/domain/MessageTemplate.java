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
 * @since 2023-08-19
 */
@Getter
@Setter
@TableName("message_template")
public class MessageTemplate implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("organization_id")
    private Integer organizationId;

    /**
     * 1-面试通知  2-面试成功通知  3-面试失败通知
     */
    @TableField("type")
    private Integer type;

    /**
     * 通知模板
     */
    @TableField("message_template")
    private String messageTemplate;

    /**
     * 逻辑删除字段
     */
    @TableField("is_deleted")
    @TableLogic
    private Byte isDeleted;
}

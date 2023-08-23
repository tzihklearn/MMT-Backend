package com.sipc.mmtbackend.pojo.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
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
 * @author tzih
 * @since 2023-08-23
 */
@Getter
@Setter
@TableName("message")
public class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("message")
    private String message;

    @TableField("time")
    private LocalDateTime time;

    /**
     * 1 接受 2 拒绝 3 待定（调整时间）
     */
    @TableField("state")
    private Integer state;

    /**
     * 1 未读 2 已读
     */
    @TableField("is_read")
    private Byte isRead;

    @TableField("organization_id")
    private Integer organizationId;

    @TableField("user_id")
    private Integer userId;

    /**
     * 消息类型：
- 1为组织发送的录取结果（无需反馈）
- 2为组织发送的面试通知（需反馈同意拒绝待定，时间冲突选待定）
- 3为“用户发送”的信息
     */
    @TableField("type")
    private Boolean type;

    @TableField("interview_status_id")
    private Integer interviewStatusId;

    @TableField("is_deleted")
    @TableLogic
    private Byte isDeleted;
}

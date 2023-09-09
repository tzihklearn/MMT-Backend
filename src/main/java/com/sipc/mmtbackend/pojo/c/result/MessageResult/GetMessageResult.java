package com.sipc.mmtbackend.pojo.c.result.MessageResult;


import lombok.Data;

@Data
public class GetMessageResult {

    /**
     * 组织头像 或 个人的头像
     */
    private String avatarUrl;

    /**
     * 组织名称
     */
    private String organization;

    /**
     * 消息内容
     */
    private String message;

    /**
     * 消息发送的时间
     */
    private String time;

    /**
     * 消息的id
     */
    private Integer messageId;

    /**
     * 消息的类型 1为组织发送的录取结果（无需反馈）2为组织发送的面试通知（需反馈同意拒绝待定，时间冲突选待定）3为“用户发送”的信息
     */
    private Integer type;

    /**
     * 用户的答复，0未反馈1同意2拒绝3待定
     */
    private Integer status;

    /**
     * 是否已读 1 未读 2 已读
     */
    private Integer isread;
}

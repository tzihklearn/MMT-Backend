package com.sipc.mmtbackend.pojo.c.result.MessageResult;

import lombok.Data;

@Data
public class MessageResult {

    /**
     * 组织id
     */
    private Integer organizationId;

    /**
     * 组织社团头像
     */
    private String avatarUrl;

    /**
     * 组织社团名称
     */
    private String organizationName;

    /**
     * 用户最新收到的一条信息（取前15个字符）
     */
    private String message;

    /**
     * 用户收到最新消息的时间
     */
    private String time;

    /**
     * 组织未读消息的数量
     */
    private Integer unread;
}

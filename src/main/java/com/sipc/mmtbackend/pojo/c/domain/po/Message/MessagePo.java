package com.sipc.mmtbackend.pojo.c.domain.po.Message;


import lombok.Data;

@Data
public class MessagePo {

    /**
     * 组织社团id
     */
    private Integer organizationId;

    /**
     * 组织社团头像
     */
    private String avatarUrl;

    /**
     * 组织社团名称
     */
    private String name;

    /**
     * 用户最新收到的一条信息
     */
    private String message;

    /**
     * 用户收到最新消息的时间
     */
    private Long time;

    /**
     * 组织未读消息的数量
     */
    private Integer count;
}

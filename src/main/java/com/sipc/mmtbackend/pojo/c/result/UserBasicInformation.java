package com.sipc.mmtbackend.pojo.c.result;

import lombok.Data;

@Data
public class UserBasicInformation {
    private String name;
    private Integer userId;
    private String phone;
    private String gender;
    private String academy;
    private String major;
    private String classNum;
}

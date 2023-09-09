package com.sipc.mmtbackend.pojo.c.result;

import lombok.Data;

@Data
public class UserInfoResult {
    private String name;
    private Integer studentId;
    private String gender;
    private String academy;
    private String major;
    private String classNum;
    private String phoneNum;
    private String email;
    private String qqNum;
}

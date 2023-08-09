package com.sipc.mmtbackend.pojo.dto.result.RealtimeIntreviewdResult.po;

import lombok.Data;

@Data
public class IntervieweePo {
    // 面试ID
    private Integer id;
    // 学号
    private String studentId;
    // 姓名
    private String name;
    // 班级
    private String className;
    // 面试部门
    private String department;
    // 面试时间
    private String time;
    // 面试地点
    private String place;
    // 是否签到
    private boolean isSigned;
    // 面试状态：0未开始，1正在面试，2已结束
    private Integer status;
}

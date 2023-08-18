package com.sipc.mmtbackend.pojo.domain.po.RealtimeInterviewPo;

import lombok.Data;

@Data
public class InterviewStatusPo {
    // 面试ID
    private Integer id;
    // 面试用户ID
    private Integer cId;
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
    // 面试状态
    private Integer state;
}

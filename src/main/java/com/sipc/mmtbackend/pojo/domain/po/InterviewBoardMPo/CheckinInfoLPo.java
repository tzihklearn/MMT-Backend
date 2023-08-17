package com.sipc.mmtbackend.pojo.domain.po.InterviewBoardMPo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CheckinInfoLPo {
    // 学号
    private String studentId;
    // 姓名
    private String name;
    // 签到时间
    private LocalDateTime time;
    // 面试地点
    private String room;
}

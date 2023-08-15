package com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardMiddleResult.po;

import lombok.Data;

@Data
public class CheckinInfoPo {
    // 学号
    private String studentId;
    // 姓名
    private String name;
    // 签到时间
    private String time;
    // 面试地点
    private String room;
}

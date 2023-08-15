package com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardMiddleResult.po;

import lombok.Data;

@Data
public class InterviewRoomProgressPo {
    // 面试地点名称
    private String name;
    // 总人数
    private Integer total;
    // 已完成人数
    private Integer finished;
}

package com.sipc.mmtbackend.pojo.domain.po.InterviewBoardMPo;

import lombok.Data;

@Data
public class InterviewProgressPo {
    // 面试地点名称
    private String name;
    // 总人数
    private Integer total;
    // 已完成人数
    private Integer finished;
}

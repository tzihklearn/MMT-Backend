package com.sipc.mmtbackend.pojo.domain.po.InterviewBoardRPo;

import lombok.Data;

@Data
public class DepartmentPassedCountPo {
    // 部门ID
    private Integer id;
    // 部门名称
    private String name;
    // 部门通过人数
    private Integer count;
}

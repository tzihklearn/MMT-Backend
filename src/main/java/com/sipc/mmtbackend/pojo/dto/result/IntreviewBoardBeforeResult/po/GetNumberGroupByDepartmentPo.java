package com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardBeforeResult.po;

import lombok.Data;

@Data
public class GetNumberGroupByDepartmentPo {
    // 部门总报名，总报名人数
    private Integer num;
    // 部门第一志愿报名人数
    private Integer firstChoiceNum;
    // 部门名称
    private String departmentName;
}

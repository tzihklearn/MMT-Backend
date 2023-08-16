package com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardResultResult;

import com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardResultResult.po.DepartmentPassCountPo;

import java.util.List;

public class GetDepartmentPassCountResult {
    // 部门数量
    private Integer count;
    private List<DepartmentPassCountPo> departments;
}

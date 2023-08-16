package com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardResultResult;

import com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardResultResult.po.DepartmentPassCountPo;
import lombok.Data;

import java.util.List;
@Data
public class GetDepartmentPassCountResult {
    // 部门数量
    private Integer count;
    private List<DepartmentPassCountPo> departments;
}

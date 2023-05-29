package com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardResult;

import com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardResult.po.GetNumberGroupByDepartmentPo;
import lombok.Data;

import java.util.List;

@Data
public class GetNumberGroupByDepartment {
    // 有报名数据的部门数
    private Integer depNum;
    // 总报名人数
    private Integer cNum;
    // 各个部门的名称与报名人数
    private List<GetNumberGroupByDepartmentPo> depNums;
}

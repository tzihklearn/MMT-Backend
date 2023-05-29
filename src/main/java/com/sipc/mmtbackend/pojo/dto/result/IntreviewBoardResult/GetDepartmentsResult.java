package com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardResult;

import com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardResult.po.GetDepartmentPo;
import lombok.Data;

import java.util.List;

@Data
public class GetDepartmentsResult {
    private Integer num;
    private List<GetDepartmentPo> departments;
}

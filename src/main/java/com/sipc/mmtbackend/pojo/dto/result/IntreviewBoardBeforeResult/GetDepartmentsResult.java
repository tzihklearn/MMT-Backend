package com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardBeforeResult;

import com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardBeforeResult.po.GetDepartmentPo;
import lombok.Data;

import java.util.List;

@Data
public class GetDepartmentsResult {
    private Integer num;
    private List<GetDepartmentPo> departments;
}

package com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardBeforeResult;

import com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardBeforeResult.po.LineChartLineDataPo;
import lombok.Data;

import java.util.List;

/**
 * B 端面试总看板获取组织各个部门报名人数随时间变化折线图数据
 *
 * @author DoudiNCer
 */
@Data
public class GetNumberGroupByTimeAndDepartmentResult {
    // 日期（横坐标）
    private List<String> date;
    // 每个部门的数据
    private List<LineChartLineDataPo> departments;
}

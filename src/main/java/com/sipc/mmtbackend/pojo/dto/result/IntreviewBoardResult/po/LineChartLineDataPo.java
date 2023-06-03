package com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardResult.po;

import lombok.Data;

import java.util.List;

/**
 * 折线图每条折线的数据
 *
 * @author DoudiNCer
 */
@Data
public class LineChartLineDataPo {
    // 折线名
    private String name;
    // 折线根据时间变化的数据
    private List<Integer> data;
}

package com.sipc.mmtbackend.pojo.domain.po.InterviewBoardRPo;

import com.sipc.mmtbackend.pojo.dto.enums.InterviewRoundEnum;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;

@Data
public class LineChartLineDataDaoPo {
    // 折线名
    private String name;
    // 折线每个点的数据
    private List<LineChartLineDataPDaoPo> data;

    /**
     * 获取横坐标
     *
     * @return 横坐标的 List
     */
    public List<String> getAbscissaData() {
        List<String> result = new LinkedList<>();
        for (LineChartLineDataPDaoPo po : data) {
            InterviewRoundEnum interviewRoundEnum = InterviewRoundEnum.checkRound(po.getRound());
            if (interviewRoundEnum == null){
                return null;
            }
            result.add(interviewRoundEnum.getName());
        }
        return result;
    }

    /**
     * 获取纵坐标数据
     *
     * @return 纵坐标数据的 List
     */
    public List<Integer> getYDatas() {
        List<Integer> result = new LinkedList<>();
        for (LineChartLineDataPDaoPo po : data)
            result.add(po.getNumber());
        return result;
    }
}

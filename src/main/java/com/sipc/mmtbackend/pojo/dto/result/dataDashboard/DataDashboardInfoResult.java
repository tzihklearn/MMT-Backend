package com.sipc.mmtbackend.pojo.dto.result.dataDashboard;

import com.sipc.mmtbackend.pojo.dto.result.dataDashboard.po.SiftBarPo;
import com.sipc.mmtbackend.pojo.dto.result.dataDashboard.po.DataDashboardInfoPo;
import lombok.Data;

import java.util.List;

/**
 * 数据面板信息返回类
 * @author tzih
 * @version v1.0
 * @since 2023.05.31
 */
@Data
public class DataDashboardInfoResult {

    /**
     * 页面顶部信息筛选栏信息
     * @see SiftBarPo
     */
    private SiftBarPo siftBar;

    /**
     * 数据面板具体的志愿报名信息
     * @see DataDashboardInfoPo
     */
    private List<DataDashboardInfoPo> interviewerInfoList;

    /**
     * 当前页面数
     */
    private Integer pageNow;

    /**
     * 全部页面数量
     */
    private Integer pageNum;

}

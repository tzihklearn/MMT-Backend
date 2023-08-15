package com.sipc.mmtbackend.pojo.dto.result.dataDashboard.po;

import lombok.Data;

/**
 * 数据面板页面顶部信息筛选栏具体筛选框单个信息实体类
 * @author tzih
 * @version v1.0
 * @since 2023.08.07
 */
@Data
public class SiftInfoPo {

    /**
     * 筛选信息info（用于存放对应筛选框对应的相关id信息，例如部门id或是社团志愿次序）,用于筛选时前端传给后端便于参数处理
     */
    private Integer info;

    /**
     * 筛选信息名称
     */
    private String siftName;

    /**
     * 筛选信息对应的人数
     */
    private Integer number;

}

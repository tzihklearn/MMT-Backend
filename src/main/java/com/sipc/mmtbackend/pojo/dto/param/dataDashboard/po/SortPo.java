package com.sipc.mmtbackend.pojo.dto.param.dataDashboard.po;

import lombok.Data;

/**
 * 数据面板筛选项排序设置
 * @author tzih
 * @version v1.0
 * @since 2023.08.07
 */
@Data
public class SortPo {

    /**
     * 排序字段，1为姓名，2为学号，3为时间，4为地点
     */
    private Integer sortId;

    /**
     * 排序方式，1为正序，2为倒序
     */
    private Integer sortBy;

}

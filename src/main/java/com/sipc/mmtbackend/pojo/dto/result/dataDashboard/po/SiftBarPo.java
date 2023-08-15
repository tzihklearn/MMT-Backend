package com.sipc.mmtbackend.pojo.dto.result.dataDashboard.po;

import lombok.Data;

import java.util.List;

/**
 * 数据面板页面顶部信息筛选栏信息实体类
 * @author tzih
 * @version v1.0
 * @since 2023.08.07
 */
@Data
public class SiftBarPo {

    /**
     * 社团志愿次序信息筛选框
     * @see SiftInfoPo
     */
    private List<SiftInfoPo> organizationOrderBar;

    /**
     * 部门志愿次序信息筛选框
     * @see SiftInfoPo
     */
    private List<SiftInfoPo> departmentOrderBar;

    /**
     * 当前志愿部门信息筛选框
     * @see SiftInfoPo
     */
    private List<SiftInfoPo> nowDepartmentBar;

    /**
     * 下一次面试时间信息筛选框
     * @see SiftInfoPo
     */
    private List<SiftInfoPo> nextTimeBar;

    /**
     * 下一次面试地点信息筛选框
     * @see SiftInfoPo
     */
    private List<SiftInfoPo> nextPlaceBar;

}

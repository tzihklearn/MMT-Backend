package com.sipc.mmtbackend.pojo.dto.param.dataDashboard;

import com.sipc.mmtbackend.pojo.dto.param.dataDashboard.po.SortPo;
import lombok.Data;

import java.util.List;

/**
 * 筛选数据面板信息接口的参数类，若不做筛选，则数组传入空数组或者null,object则传null
 * @author tzih
 * @version v1.0
 * @since 2023.08.07
 */
@Data
public class SiftParam {

    /**
     * 面试轮次筛选项，1为一面，2为二面，3为三面
     */
    private List<Integer> interviewRoundSift;

    /**
     * 面试情况筛选项，1为未开始，2为进行中，3为失败，4为成功
     */
    private List<Integer> interviewStatusSift;

    /**
     * 模糊搜索参数，仅支持对姓名、学号、班级、手机号进行模糊搜索
     */
    private String search;

    /**
     * 排序相关参数
     * @see SortPo
     */
    private SortPo sort;

    /**
     * 社团志愿次序筛选项，1为第一志愿，2为第二志愿，3为第三志愿，...，以此类推，也可以直接使用在获取数据面板信息时的顶部筛选栏对应的筛选项中存放的相关筛选框(siftBar/xxxBar/*)info信息
     */
    private List<Integer> organizationOrderSift;

    /**
     * 部门志愿筛选项，1为第一志愿，2为第二志愿，3为第三志愿，...，以此类推
     */
    private List<Integer> departmentOrderSift;

    /**
     * 当前志愿部门筛选项，填入参数为志愿部门id,在获取数据面板信息时的顶部筛选栏对应的筛选项中存放了相关部门id信息（即相关的筛选框(siftBar/xxxBar/*)的info）
     */
    private List<Integer> nowDepartmentSift;

    /**
     * 下一次面试时间筛选项，直接按照原格式传入要筛选的时间
     */
    private List<String> nextTimeSift;

    /**
     * 下一次面试地点筛选项，填入参数为地点id,可以直接使用在获取数据面板信息时的顶部筛选栏对应的筛选项中存放的相关筛选框(siftBar/xxxBar/*)info信息
     */
    private List<Integer> nextPlaceSift;

}

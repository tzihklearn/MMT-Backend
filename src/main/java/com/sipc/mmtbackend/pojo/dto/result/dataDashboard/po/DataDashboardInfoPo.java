package com.sipc.mmtbackend.pojo.dto.result.dataDashboard.po;

import lombok.Data;

/**
 * 数据面板中每一条具体志愿信息的类
 * @author tzih
 * @version v1.0
 * @since 2023.05.31
 */
@Data
public class DataDashboardInfoPo {

    /**
     * 该条志愿信息id
     */
    private Integer id;

    /**
     * 学号
     */
    private String studentId;

    /**
     * 学生姓名
     */
    private String name;

    /**
     * 班级名称
     */
    private String className;

    /**
     * 学生手机号，会进行脱敏处理
     */
    private String phone;

    /**
     * 社团志愿次序
     */
    private String organizationOrder;

    /**
     * 部门志愿次序
     */
    private String departmentOrder;

    /**
     * 当前志愿部门
     */
    private String nowDepartment;

    /**
     * 志愿状态
     */
    private String volunteerStatus;

    /**
     * 下一次面试时间
     */
    private String nextTime;

    /**
     * 下一次面试地点
     */
    private String nextPlace;

}

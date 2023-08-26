package com.sipc.mmtbackend.pojo.dto.result.interviewArrangement.po;

import lombok.Data;

/**
 * @author tzih
 * @version v1.0
 * @since 2023.08.23
 */
@Data
public class IAInfoPo {
    /**
     * 该条志愿信息id
     */
    private Integer id;

    private Integer userId;

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
     * 当前面试部门
     */
    private String nowDepartment;

    /**
     * 上一次志愿状态
     */
    private String interviewStatus;

    /**
     * 下一次面试时间
     */
    private String nextTime;

    /**
     * 下一次面试地点
     */
    private String nextPlace;

    /**
     * 面试通知状态
     */
    private Integer messageStatus;

}

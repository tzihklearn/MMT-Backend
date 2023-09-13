package com.sipc.mmtbackend.pojo.domain.po;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author tzih
 * @version v1.0
 * @since 2023.08.23
 */
@Data
public class InterviewMessagePo {
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
    private Integer majorClassId;


    /**
     * 当前面试部门
     */
    private Integer departmentId;

    /**
     * 上一次志愿状态
     */
    private Integer state;

    /**
     * 下一次面试时间
     */
    private LocalDateTime nextTime;

    /**
     * 下一次面试地点
     */
    private Integer nextPlaceId;

    /**
     * 面试通知状态
     */
    private Integer messageStatus;
}

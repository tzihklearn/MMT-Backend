package com.sipc.mmtbackend.pojo.domain.po;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author tzih
 * @version v1.0
 * @since 2023.08.17
 */
@Data
public class MyInterviewStatusPo {

    private Integer id;

    /**
     * 面试者id
     */
    private Integer userId;

    /**
     * 面试地点id
     */
    private Integer admissionAddressId;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    private Integer state;

    /**
     * 签到时间
     */
    private LocalDateTime signInTime;

    /**
     * 社团志愿次序
     */
    private Integer organizationOrder;

    /**
     * 部门志愿次序
     */
    private Integer departmentOrder;

    /**
     * 面试社团id,冗余字段
     */
    private Integer admissionId;

    /**
     * 面试部门id,冗余字段
     */
    private Integer departmentId;

    /**
     * 面试轮次。冗余字段
     */
    private Integer round;

    private String studentId;

    private String name;

    private Integer majorClassId;

    private String phone;

}

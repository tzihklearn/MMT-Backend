package com.sipc.mmtbackend.pojo.c.result;

import lombok.Data;

@Data
public class PersonalInfoArrangeResult {
    private String organizationName;
    private String departmentName;
    private String startTime;
    private String address;
    private String round;
    private Boolean status;
    private String png;
}

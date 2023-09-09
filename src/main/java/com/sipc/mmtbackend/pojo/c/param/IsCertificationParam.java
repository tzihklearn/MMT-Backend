package com.sipc.mmtbackend.pojo.c.param;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class IsCertificationParam {
    @NotNull
    private Integer userId;
    @NotNull
    private Integer studentId;
    @NotBlank
    private String name;
    @NotBlank
    private String gender;
    @NotBlank
    private String academy;
    @NotBlank
    private String major;
    @NotBlank
    private String classNum;
    @NotNull
    private Double height;
    @NotNull
    private Double weight;
    @NotBlank
    private String birthday;
    @NotNull
    private Boolean is_certification;
}

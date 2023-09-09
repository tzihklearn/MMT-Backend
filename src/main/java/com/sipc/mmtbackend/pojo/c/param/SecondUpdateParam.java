package com.sipc.mmtbackend.pojo.c.param;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class SecondUpdateParam {
    @NotBlank
    private String phoneNum;
    @NotBlank
    private String email;
    @NotBlank
    private String qqNum;
}

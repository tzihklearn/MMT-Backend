package com.sipc.mmtbackend.pojo.dto.param.UserBParam;

import lombok.Data;

@Data
public class PutUserPasswordParam {
    private String oldPassword;
    private String newPassword;
}

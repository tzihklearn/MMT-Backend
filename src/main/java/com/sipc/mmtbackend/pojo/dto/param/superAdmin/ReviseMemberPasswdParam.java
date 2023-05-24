package com.sipc.mmtbackend.pojo.dto.param.superAdmin;

import lombok.Data;

/**
 * @author tzih
 * @version v1.0
 * @since 2023.05.22
 */
@Data
public class ReviseMemberPasswdParam {

    private Integer userId;

    private String passwd;

}

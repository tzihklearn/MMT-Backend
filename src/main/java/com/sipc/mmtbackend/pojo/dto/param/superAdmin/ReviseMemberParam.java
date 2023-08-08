package com.sipc.mmtbackend.pojo.dto.param.superAdmin;

import lombok.Data;

/**
 * @author tzih
 * @version v1.0
 * @since 2023.08.08
 */
@Data
public class ReviseMemberParam {

    private Integer id;

    private String name;

    private String studentId;

    private String phone;

    private String permission;

    private String passwd;

}

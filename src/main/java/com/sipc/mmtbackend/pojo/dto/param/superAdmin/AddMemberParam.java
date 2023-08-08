package com.sipc.mmtbackend.pojo.dto.param.superAdmin;

import lombok.Data;

/**
 * @author tzih
 * @version v1.0
 * @since 2023.08.08
 */
@Data
public class AddMemberParam {

    private String studentId;

    private String name;

    private String phone;

    private String permission;

    private String passwd;

}

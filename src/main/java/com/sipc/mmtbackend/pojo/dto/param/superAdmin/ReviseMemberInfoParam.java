package com.sipc.mmtbackend.pojo.dto.param.superAdmin;

import lombok.Data;

/**
 * @author tzih
 * @version v1.0
 * @since 2023.05.22
 */
@Data
public class ReviseMemberInfoParam {

    private Integer id;

    private String name;

    private String studentId;

    private String permission;

}

package com.sipc.mmtbackend.pojo.domain.po;

import lombok.Data;

/**
 * @author tzih
 * @version v1.0
 * @since 2023.05.21
 */
@Data
public class UserBMemberPo {

    Integer id;

    String studentId;

    String userName;

    String phone;

    Integer permissionId;

}

package com.sipc.mmtbackend.pojo.dto.data;

import lombok.Data;

/**
 * 社团成员信息实体类
 *
 * @author tzih
 * @version v1.0
 * @since 2023.05.21
 */
@Data
public class MemberInfoData {

    /**
     * 社团成员用户id
     */
    Integer id;

    /**
     * 社团成员学号
     */
    String studentId;

    /**
     * 社团成员姓名
     */
    String name;

    /**
     * 社团成员权限
     */
    String permission;

    /**
     * 社团成员手机号
     */
    String phone;

}

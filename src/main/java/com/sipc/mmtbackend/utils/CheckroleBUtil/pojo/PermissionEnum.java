package com.sipc.mmtbackend.utils.CheckroleBUtil.pojo;

import lombok.Getter;
import lombok.ToString;

/**
 * B 端权限名称与代码
 *
 * @author DoudiNCerr
 */
@Getter
@ToString
public enum PermissionEnum {
    SUPER_ADMIN(1, "SuperAdmin"),
    COMMITTEE(2, "Committee"),
    MEMBER(3, "Member");

    private final Integer id;
    private final String name;

    PermissionEnum(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
}

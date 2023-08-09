package com.sipc.mmtbackend.annotation;

import com.sipc.mmtbackend.utils.CheckroleBUtil.pojo.PermissionEnum;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface BPermission {

//    @AliasFor("permission")
    PermissionEnum value() default PermissionEnum.SUPER_ADMIN;

}
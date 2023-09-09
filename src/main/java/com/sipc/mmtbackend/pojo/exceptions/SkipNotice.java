package com.sipc.mmtbackend.pojo.exceptions;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface SkipNotice {

}

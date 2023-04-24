package com.sipc.mmtbackend.pojo.dto.param;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author tzih
 * @version v1.0
 * @since 2023.04.23
 */
@Data
public class DepartmentData {

    private Integer id;

    @NotNull(message = "部门名称不能为null")
    @Size(min = 2, max = 20, message = "部门名称错误，字数范围为[1,5]")
    private String name;
    @Size(min = 2, max = 40, message = "部门简介错误，字数范围为[1,20]")
    private String briefIntroduction;
    @Size(min = 2, max = 400, message = "部门介绍错误，字数范围为[1,200]")
    private String introduction;
    @Size(min = 2, max = 2000, message = "部门纳新标准错误，字数范围为[1,1000]")
    private String standard;
}

package com.sipc.mmtbackend.pojo.dto.data;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 社团纳新部门相关信息的封装实体类
 * @author tzih
 * @version v1.0
 * @since 2023.04.23
 */
@Data
@AllArgsConstructor
public class DepartmentData {

    /**
     * 社团纳新部门id,可以为null
     */
    private Integer id;

    /**
     * 社团纳新部门名称
     */
    @NotNull(message = "部门名称不能为null")
    @Size(min = 2, max = 20, message = "部门名称错误，字数范围为[1,5]")
    private String name;

    /**
     * 社团纳新部门简介
     */
    @Size(min = 2, max = 40, message = "部门简介错误，字数范围为[1,20]")
    private String briefIntroduction;

    /**
     * 社团纳新部门介绍
     */
    @Size(min = 2, max = 400, message = "部门介绍错误，字数范围为[1,200]")
    private String introduction;

    /**
     * 社团纳新部门标准
     */
    @Size(min = 2, max = 2000, message = "部门纳新标准错误，字数范围为[1,1000]")
    private String standard;

    public DepartmentData() {

    }

}

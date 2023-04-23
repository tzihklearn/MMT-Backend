package com.sipc.mmtbackend.pojo.dto.param;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @author tzih
 * @version v1.0
 * @since 2023.04.23
 */
@Data
public class OrganizationInfoParam {

    @NotNull(message = "社团id不能为空")
    private Integer organizationId;

    @NotBlank(message = "社团名称不能为null")
    @Size(min = 2, max = 10, message = "社团名称长度错误，字数范围为[1,5]")
    private String name;


    @NotNull(message = "简介不能为null")
    @Size(min = 2, max = 40, message = "简介长度错误，字数范围为[1,20]")
    private String briefIntroduction;

    /**
     * 标签
     */
    private List<TagData> tagList;

    @NotNull(message = "介绍不能为null")
    @Size(min = 2, max = 400, message = "介绍长度错误，字数范围为[1,200]")
    private String introduction;

    //TODO:删除了参数检验
//    @Size(min = 2, max = 200, message = "社团特色错误，字数范围为[1,100]")
    /**
     * 社团特色
     */
    private String feature;


    @Size(min = 2, max = 200, message = "社团日常错误，字数范围为[1,100]")
    private String daily;

    @Size(min = 2, max = 200, message = "社团宣言错误，字数范围为[1,100]")
    private String slogan;

    @Size(min = 2, max = 200, message = "社团联系方式错误，字数范围为[1,100]")
    private String contactInfo;

    @Size(min = 2, max = 200, message = "社团更多错误，字数范围为[1,100]")
    private String more;

    /**
     * 部门列表
     */
    @Valid
    private List<DepartmentData> departmentList;
}

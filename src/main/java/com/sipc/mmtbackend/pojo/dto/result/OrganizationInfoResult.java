package com.sipc.mmtbackend.pojo.dto.result;

import com.sipc.mmtbackend.pojo.dto.param.DepartmentData;
import com.sipc.mmtbackend.pojo.dto.param.TagData;
import lombok.Data;
import java.util.List;

/**
 * 获取社团宣传信息接口的返回类，封装了相应的社团信息
 * @author tzih
 * @version v1.0
 * @since 2023.04.27
 */
@Data
public class OrganizationInfoResult {

    /**
     * 社团id
     */
    private Integer organizationId;

    /**
     * 社团名称
     */
    private String name;

    /**
     * 社团头像路径
     */
    private String avatarUrl;


    /**
     * 社团简介
     */
    private String briefIntroduction;

    /**
     * 标签
     */
    private List<TagData> tagList;

    /**
     * 社团介绍
     */
    private String introduction;

    //TODO:删除了参数检验
//    @Size(min = 2, max = 200, message = "社团特色错误，字数范围为[1,100]")
    /**
     * 社团特色
     */
    private String feature;


    /**
     * 社团日常
     */
    private String daily;

    /**
     * 社团宣言
     */
    private String slogan;

    /**
     * 社团联系方式
     */
    private String contactInfo;

    /**
     * 社团更多信息
     */
    private String more;

    /**
     * 部门列表
     */
    private List<DepartmentData> departmentList;

}
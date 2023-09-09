package com.sipc.mmtbackend.pojo.c.param.SuggestionParam;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
public class SuggestionParam {

    @NotNull(message = "建议类型代码不能为空")
    @Range(min = 1, max = 4, message = "建议类型代码格式错误")
    private Integer suggestionType;

    @NotNull(message = "反馈内容不能为空")
    private String discribe;

    @NotNull(message = "联系方式不能为空")
    private String phone;

    @NotNull(message = "是否允许开发者联系用户不能为空")
    @Range(min = 1, max = 2, message = "是否允许开发者联系用户代码格式错误")
    private Integer call;

    private List<String> photoCode = new ArrayList<>();
}

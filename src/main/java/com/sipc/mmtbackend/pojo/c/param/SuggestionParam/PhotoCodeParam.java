package com.sipc.mmtbackend.pojo.c.param.SuggestionParam;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class PhotoCodeParam {
    @NotNull(message = "图片代码不能为空")
    private String photoCode;
}

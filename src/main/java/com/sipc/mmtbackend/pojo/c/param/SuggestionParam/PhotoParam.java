package com.sipc.mmtbackend.pojo.c.param.SuggestionParam;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

@Data
public class PhotoParam {
    @NotNull(message = "图片不能为空")
    private MultipartFile file;
}

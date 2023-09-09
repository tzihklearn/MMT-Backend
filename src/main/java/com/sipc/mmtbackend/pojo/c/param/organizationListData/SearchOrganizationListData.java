package com.sipc.mmtbackend.pojo.c.param.organizationListData;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class SearchOrganizationListData {
    @JsonProperty("keyWord")
    @NotNull(message = "搜索关键词不得为空")
    private String keyWord;
}

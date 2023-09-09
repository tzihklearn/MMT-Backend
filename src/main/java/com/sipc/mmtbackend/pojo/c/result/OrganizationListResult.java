package com.sipc.mmtbackend.pojo.c.result;

import com.sipc.mmtbackend.pojo.c.param.organizationListData.OrganizationListData;
import lombok.Data;

import java.util.List;

@Data
public class OrganizationListResult {

    private List<OrganizationListData> organizationListDataList;
    private Integer totalNum;

    public OrganizationListResult() {
    }
}

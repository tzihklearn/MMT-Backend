package com.sipc.mmtbackend.service.c;

import com.sipc.mmtbackend.pojo.c.param.organizationListData.SearchOrganizationListData;
import com.sipc.mmtbackend.pojo.c.result.OrganizationListResult;
import com.sipc.mmtbackend.pojo.dto.CommonResult;

public interface GetOrganizationListService {

    CommonResult<OrganizationListResult> getOrganizationListService();

    CommonResult<OrganizationListResult> searchOrganizationList(SearchOrganizationListData searchOrganizationListData);

}

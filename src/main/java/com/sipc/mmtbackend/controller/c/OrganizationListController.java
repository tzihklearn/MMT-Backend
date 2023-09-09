package com.sipc.mmtbackend.controller.c;

import com.sipc.mmtbackend.pojo.c.param.organizationListData.SearchOrganizationListData;
import com.sipc.mmtbackend.pojo.c.result.OrganizationListResult;
import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.service.c.GetOrganizationListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author 汤子涵
 */

@RestController
@RequestMapping(value = "/organization-list", method = RequestMethod.GET)
public class OrganizationListController {

    private final GetOrganizationListService getOrganizationListService;

    @Autowired
    public OrganizationListController(GetOrganizationListService getOrganizationListService) {
        this.getOrganizationListService = getOrganizationListService;
    }

    @GetMapping("/all")
    public CommonResult<OrganizationListResult> getOrganizationList() {
        return getOrganizationListService.getOrganizationListService();
    }

    @PostMapping("/keyWord")
    public CommonResult<OrganizationListResult> searchOrganizationList(@Valid @RequestBody SearchOrganizationListData searchOrganizationListData) {
        return getOrganizationListService.searchOrganizationList(searchOrganizationListData);
    }

}

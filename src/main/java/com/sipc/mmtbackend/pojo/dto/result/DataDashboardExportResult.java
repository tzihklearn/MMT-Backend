package com.sipc.mmtbackend.pojo.dto.result;

import com.sipc.mmtbackend.pojo.dto.result.dataDashboard.po.DataDashboardInfoPo;
import lombok.Data;

import java.util.List;

/**
 * @author tzih
 * @version v1.0
 * @since 2023.08.16
 */
@Data
public class DataDashboardExportResult {

    private List<DataDashboardInfoPo> interviewerInfoList;

}

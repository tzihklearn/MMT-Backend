package com.sipc.mmtbackend.pojo.dto.result.interviewArrangement;

import com.sipc.mmtbackend.pojo.dto.result.interviewArrangement.po.SiftBarPo;
import lombok.Data;

import java.util.List;

/**
 * @author tzih
 * @version v1.0
 * @since 2023.08.23
 */
@Data
public class SiftBarResult {

    private List<SiftBarPo> departmentList;

    private List<SiftBarPo> addressIdList;

    private List<SiftBarPo> messageStatusList;

}
